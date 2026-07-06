import Ionicons from "@expo/vector-icons/Ionicons";
import { useMisCompras } from "@/src/hooks/useMisCompras";
import { EstadoVenta, VentaResumenResponse } from "@/src/dto/CompraDTO";
import { getCurrencyCode } from "@/src/utils/moneda";
import { router, useFocusEffect } from "expo-router";
import { useCallback } from "react";
import {
  ActivityIndicator,
  Image,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

const defaultImage = require("@/src/assets/images/obras_arte.jpg");

function formatPrice(moneda: string, amount: number) {
  return `${getCurrencyCode(moneda)} ${amount}`;
}

function estadoLabel(estado: EstadoVenta) {
  const labels: Record<EstadoVenta, string> = {
    PENDIENTE_PAGO: "Pendiente de pago",
    PAGO_CONFIRMADO: "Pago confirmado",
    PREPARANDO_ENVIO: "Preparando envio",
    ENVIADO: "Enviado",
    EN_CAMINO: "En camino",
    ENTREGADO: "Entregado",
    MULTADA: "Multada",
    INCUMPLIDA: "Incumplida",
    CANCELADA: "Cancelada",
  };

  return labels[estado] ?? estado;
}

function CompraCard({ compra }: { compra: VentaResumenResponse }) {
  const pendiente = compra.estado === "PENDIENTE_PAGO";

  return (
    <Pressable
      style={styles.card}
      onPress={() =>
        router.push({
          pathname: "/(tabs)/compras/[idVenta]" as any,
          params: { idVenta: String(compra.idVenta) },
        })
      }
    >
      <Image
        source={compra.imagenUrl ? { uri: compra.imagenUrl } : defaultImage}
        style={styles.image}
        resizeMode="cover"
      />

      <View style={styles.cardBody}>
        <View style={styles.cardTopRow}>
          <Text style={styles.itemTitle} numberOfLines={2}>
            {compra.tituloItem}
          </Text>
          <Ionicons name="chevron-forward" size={20} color="#64748B" />
        </View>

        <Text style={styles.total}>{formatPrice(compra.moneda, compra.total)}</Text>
        <Text style={[styles.status, pendiente && styles.statusPending]}>
          {estadoLabel(compra.estado)}
        </Text>

        {pendiente ? (
          <Text style={styles.ctaText}>Completar compra</Text>
        ) : null}
      </View>
    </Pressable>
  );
}

export default function MisComprasScreen() {
  const { compras, loading, error, recargar } = useMisCompras();

  useFocusEffect(
    useCallback(() => {
      void recargar();
    }, [recargar]),
  );

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.content}
      refreshControl={
        <RefreshControl refreshing={loading} onRefresh={recargar} />
      }
    >
      <Text style={styles.title}>Mis compras</Text>

      {loading && compras.length === 0 ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.stateText}>Cargando compras...</Text>
        </View>
      ) : null}

      {!loading && error ? (
        <View style={styles.stateCard}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={recargar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : null}

      {!loading && !error && compras.length === 0 ? (
        <View style={styles.stateCard}>
          <Ionicons name="bag-check-outline" size={28} color="#64748B" />
          <Text style={styles.emptyText}>Todavia no tenes compras pendientes.</Text>
        </View>
      ) : null}

      {compras.map((compra) => (
        <CompraCard key={compra.idVenta} compra={compra} />
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: { padding: 18, paddingBottom: 36 },
  title: {
    fontSize: 28,
    fontWeight: "900",
    color: "#0F172A",
    marginBottom: 18,
  },
  card: {
    flexDirection: "row",
    gap: 12,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 12,
    marginBottom: 14,
  },
  image: {
    width: 92,
    height: 92,
    borderRadius: 12,
    backgroundColor: "#E2E8F0",
  },
  cardBody: { flex: 1 },
  cardTopRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: 8,
  },
  itemTitle: {
    flex: 1,
    fontSize: 16,
    lineHeight: 21,
    fontWeight: "800",
    color: "#0F172A",
  },
  total: {
    marginTop: 8,
    fontSize: 17,
    fontWeight: "900",
    color: "#111827",
  },
  status: {
    marginTop: 5,
    fontSize: 12,
    fontWeight: "800",
    color: "#475569",
  },
  statusPending: { color: "#B45309" },
  ctaText: {
    marginTop: 8,
    fontSize: 13,
    fontWeight: "900",
    color: "#2563EB",
  },
  stateCard: {
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 20,
  },
  stateText: {
    marginTop: 8,
    color: "#64748B",
    fontWeight: "700",
  },
  emptyText: {
    marginTop: 8,
    color: "#64748B",
    fontWeight: "800",
    textAlign: "center",
  },
  errorText: {
    color: "#B91C1C",
    fontWeight: "800",
    textAlign: "center",
  },
  retryButton: {
    marginTop: 12,
    backgroundColor: "#2F63F6",
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 10,
  },
  retryText: { color: "#FFFFFF", fontWeight: "800" },
});
