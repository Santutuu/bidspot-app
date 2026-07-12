import { eliminarTarjeta } from "@/src/api/meAPI";
import { TarjetaResponseDTO } from "@/src/dto/me/TarjetaDTO";
import { useTarjetas } from "@/src/hooks/useTarjetas";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Pressable,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

const MAX_TARJETAS = 3;

function getCardId(tarjeta: TarjetaResponseDTO) {
  return tarjeta.idTarjeta ?? tarjeta.idMedioPago;
}

function getMaskedNumber(tarjeta: TarjetaResponseDTO) {
  return tarjeta.numeroEnmascarado ?? tarjeta.numero ?? "****";
}

function getBrand(masked: string) {
  const normalized = masked.toLowerCase();
  if (normalized.includes("master")) return "MASTERCARD";
  if (normalized.includes("visa")) return "VISA";
  return "CARD";
}

export default function TarjetasScreen() {
  const { tarjetas, loading, refreshing, error, cargarTarjetas, refrescar } =
    useTarjetas();
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useFocusEffect(
    useCallback(() => {
      void cargarTarjetas();
    }, [cargarTarjetas]),
  );

  async function deleteCard(tarjeta: TarjetaResponseDTO) {
    const id = getCardId(tarjeta);
    if (!id) return;

    Alert.alert("Eliminar tarjeta", "Esta accion no se puede deshacer.", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Eliminar",
        style: "destructive",
        onPress: async () => {
          try {
            setDeletingId(id);
            await eliminarTarjeta(id);
            await cargarTarjetas(true);
          } catch (err: any) {
            Alert.alert(
              "Error",
              err.response?.data?.message ??
                err.response?.data?.error ??
                "No pudimos eliminar la tarjeta.",
            );
          } finally {
            setDeletingId(null);
          }
        },
      },
    ]);
  }

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={refrescar} />
      }
    >
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace("/(tabs)/financial-setup" as any)}
      >
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <View style={styles.titleRow}>
        <Text style={styles.title}>Tarjetas</Text>
        {tarjetas.length < MAX_TARJETAS && (
          <Pressable
            style={styles.addIcon}
            onPress={() =>
              router.push("/(tabs)/financial-setup/tarjeta-form" as any)
            }
          >
            <Ionicons name="add" size={24} color="#FFFFFF" />
          </Pressable>
        )}
      </View>

      {tarjetas.length >= MAX_TARJETAS && (
        <Text style={styles.limitText}>
          Ya alcanzaste el máximo de 3 tarjetas.
        </Text>
      )}

      {loading ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.stateText}>Cargando tarjetas...</Text>
        </View>
      ) : error ? (
        <View style={styles.stateCard}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={refrescar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : tarjetas.length === 0 ? (
        <View style={styles.stateCard}>
          <Ionicons name="card-outline" size={36} color="#2F63F6" />
          <Text style={styles.emptyTitle}>No tenes tarjetas cargadas</Text>
          <Pressable
            style={styles.primaryButton}
            onPress={() =>
              router.push("/(tabs)/financial-setup/tarjeta-form" as any)
            }
          >
            <Text style={styles.primaryText}>Agregar tarjeta</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.cardsList}>
          {tarjetas.map((tarjeta, index) => {
            const masked = getMaskedNumber(tarjeta);
            const brand = getBrand(masked);
            const id = getCardId(tarjeta);

            return (
              <View key={id ?? index} style={styles.paymentCard}>
                <View style={styles.cardTopRow}>
                  <Text style={styles.brandText}>{brand}</Text>
                  <Pressable
                    style={styles.deleteButton}
                    onPress={() => deleteCard(tarjeta)}
                    disabled={deletingId === id}
                  >
                    {deletingId === id ? (
                      <ActivityIndicator size="small" color="#DC2626" />
                    ) : (
                      <Ionicons
                        name="trash-outline"
                        size={18}
                        color="#DC2626"
                      />
                    )}
                  </Pressable>
                </View>

                {tarjeta.principal && (
                  <Text style={styles.principalBadge}>Principal</Text>
                )}

                <Text style={styles.maskedNumber}>{masked}</Text>
                <Text style={styles.holder}>{tarjeta.nombre}</Text>
                <Text style={styles.expiration}>Vence {tarjeta.fechaVto}</Text>
              </View>
            );
          })}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  container: { paddingHorizontal: 20, paddingTop: 20, paddingBottom: 36 },
  backButton: { width: 42, height: 42, justifyContent: "center" },
  titleRow: {
    marginTop: 8,
    marginBottom: 12,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  title: { fontSize: 30, fontWeight: "900", color: "#0F172A" },
  addIcon: {
    width: 38,
    height: 38,
    borderRadius: 19,
    backgroundColor: "#2F63F6",
    alignItems: "center",
    justifyContent: "center",
  },
  limitText: {
    color: "#B45309",
    backgroundColor: "#FEF3C7",
    borderRadius: 12,
    padding: 12,
    fontSize: 14,
    fontWeight: "800",
    marginBottom: 14,
  },
  cardsList: { gap: 14 },
  paymentCard: {
    width: "92%",
    alignSelf: "center",
    minHeight: 150,
    borderRadius: 18,
    borderWidth: 1.5,
    borderColor: "#111827",
    backgroundColor: "#FFD84D",
    padding: 16,
    shadowColor: "#0F172A",
    shadowOpacity: 0.12,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 8 },
    elevation: 3,
  },
  cardTopRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
  },
  brandText: {
    alignSelf: "flex-start",
    backgroundColor: "#FFFFFF",
    color: "#1D4ED8",
    fontSize: 18,
    fontWeight: "900",
    fontStyle: "italic",
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 6,
  },
  deleteButton: {
    width: 34,
    height: 34,
    alignItems: "center",
    justifyContent: "center",
  },
  principalBadge: {
    alignSelf: "flex-start",
    backgroundColor: "#111827",
    color: "#FFFFFF",
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 4,
    fontSize: 12,
    fontWeight: "900",
    marginBottom: 8,
  },
  maskedNumber: {
    textAlign: "right",
    fontSize: 26,
    color: "#111827",
    fontWeight: "900",
    letterSpacing: 1,
    marginTop: 6,
  },
  holder: {
    marginTop: 10,
    color: "#111827",
    fontSize: 14,
    fontWeight: "800",
    textTransform: "uppercase",
  },
  expiration: {
    marginTop: 4,
    color: "#374151",
    fontSize: 13,
    fontWeight: "700",
  },
  stateCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 20,
    alignItems: "center",
    gap: 10,
  },
  emptyTitle: {
    fontSize: 18,
    fontWeight: "900",
    color: "#0F172A",
    textAlign: "center",
  },
  stateText: { fontSize: 14, color: "#64748B", fontWeight: "700" },
  errorText: {
    fontSize: 14,
    color: "#B91C1C",
    fontWeight: "800",
    textAlign: "center",
  },
  retryButton: {
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  retryText: { color: "#111827", fontSize: 14, fontWeight: "900" },
  primaryButton: {
    marginTop: 8,
    backgroundColor: "#2F63F6",
    borderRadius: 14,
    paddingHorizontal: 18,
    paddingVertical: 13,
  },
  primaryText: { color: "#FFFFFF", fontSize: 15, fontWeight: "900" },
});
