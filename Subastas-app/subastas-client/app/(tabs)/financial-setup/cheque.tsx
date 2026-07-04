import { eliminarCheque } from "@/src/api/meAPI";
import { ChequeResponseDTO } from "@/src/dto/me/ChequeDTO";
import { useCheques } from "@/src/hooks/useCheques";
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

const MAX_CHEQUES = 3;

function getChequeId(cheque: ChequeResponseDTO) {
  return cheque.idCheque ?? cheque.idMedioPago;
}

function formatMoney(value: number) {
  return `$${value}`;
}

export default function ChequesScreen() {
  const { cheques, loading, refreshing, error, cargarCheques, refrescar } =
    useCheques();
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useFocusEffect(
    useCallback(() => {
      void cargarCheques();
    }, [cargarCheques]),
  );

  function deleteCheque(cheque: ChequeResponseDTO) {
    const id = getChequeId(cheque);
    if (!id) return;

    Alert.alert("Eliminar cheque", "Esta accion no se puede deshacer.", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Eliminar",
        style: "destructive",
        onPress: async () => {
          try {
            setDeletingId(id);
            await eliminarCheque(id);
            await cargarCheques(true);
          } catch (err: any) {
            Alert.alert(
              "Error",
              err.response?.data?.message ??
                err.response?.data?.error ??
                "No pudimos eliminar el cheque.",
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
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <Text style={styles.title}>Cheques</Text>

      {cheques.length < MAX_CHEQUES ? (
        <Pressable
          style={styles.addRow}
          onPress={() =>
            router.push("/(tabs)/financial-setup/cheque-form" as any)
          }
        >
          <Text style={styles.addText}>Agregar cheque</Text>
          <Ionicons name="add" size={22} color="#111827" />
        </Pressable>
      ) : (
        <Text style={styles.limitText}>
          Ya alcanzaste el máximo de 3 cheques.
        </Text>
      )}

      {loading ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.stateText}>Cargando cheques...</Text>
        </View>
      ) : error ? (
        <View style={styles.stateCard}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={refrescar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : cheques.length === 0 ? (
        <View style={styles.stateCard}>
          <Ionicons name="document-text-outline" size={36} color="#2F63F6" />
          <Text style={styles.emptyTitle}>No tenes cheques cargados</Text>
        </View>
      ) : (
        <View style={styles.chequeList}>
          {cheques.map((cheque, index) => {
            const id = getChequeId(cheque);

            return (
              <View key={id ?? index} style={styles.chequeCard}>
                <View style={styles.chequeTop}>
                  <Text style={styles.chequeNumber}>#{cheque.nroCheque}</Text>
                  {cheque.estado ? (
                    <View style={styles.statusPill}>
                      <Text style={styles.statusText}>{cheque.estado}</Text>
                    </View>
                  ) : null}
                </View>

                <Text style={styles.beneficiary}>{cheque.beneficiario}</Text>
                <Text style={styles.metaText}>CUIL/CUIT {cheque.cuilCuit}</Text>
                <Text style={styles.amount}>{formatMoney(cheque.saldo)}</Text>

                <Pressable
                  style={styles.deleteButton}
                  onPress={() => deleteCheque(cheque)}
                  disabled={deletingId === id}
                >
                  {deletingId === id ? (
                    <ActivityIndicator size="small" color="#DC2626" />
                  ) : (
                    <Ionicons name="trash-outline" size={18} color="#DC2626" />
                  )}
                </Pressable>
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
  title: {
    marginTop: 10,
    marginBottom: 10,
    fontSize: 30,
    fontWeight: "900",
    color: "#0F172A",
  },
  addRow: {
    alignSelf: "flex-start",
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    marginBottom: 14,
  },
  addText: { fontSize: 16, color: "#111827", fontWeight: "800" },
  limitText: {
    color: "#B45309",
    backgroundColor: "#FEF3C7",
    borderRadius: 12,
    padding: 12,
    fontSize: 14,
    fontWeight: "800",
    marginBottom: 14,
  },
  chequeList: { gap: 14 },
  chequeCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1.3,
    borderColor: "#111827",
    padding: 16,
    minHeight: 132,
    position: "relative",
  },
  chequeTop: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
  },
  chequeNumber: { fontSize: 17, color: "#111827", fontWeight: "900" },
  statusPill: {
    borderWidth: 1,
    borderColor: "#22C55E",
    borderRadius: 999,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  statusText: { color: "#15803D", fontSize: 12, fontWeight: "900" },
  beneficiary: {
    color: "#111827",
    fontSize: 16,
    fontWeight: "800",
    marginBottom: 4,
  },
  metaText: { color: "#64748B", fontSize: 13, fontWeight: "700" },
  amount: {
    color: "#111827",
    fontSize: 20,
    fontWeight: "900",
    marginTop: 12,
  },
  deleteButton: {
    position: "absolute",
    right: 12,
    bottom: 12,
    width: 34,
    height: 34,
    alignItems: "center",
    justifyContent: "center",
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
    color: "#0F172A",
    fontSize: 18,
    fontWeight: "900",
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
});
