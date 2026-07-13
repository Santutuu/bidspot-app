import Ionicons from "@expo/vector-icons/Ionicons";
import { useMisCompras } from "@/src/hooks/useMisCompras";
import { PenalizacionResponse, VentaResumenResponse } from "@/src/dto/CompraDTO";
import { useMisPenalizaciones } from "@/src/hooks/useMisPenalizaciones";
import {
  formatCurrency,
  formatDate,
  getEstadoVentaLabel,
} from "@/src/utils/venta";
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

function CompraCard({ compra }: { compra: VentaResumenResponse }) {
  const pendiente = compra.estado === "PENDIENTE_PAGO";
  const action =
    compra.estado === "PENDIENTE_PAGO"
      ? "Completar compra"
      : compra.estado === "INCUMPLIDA"
        ? "Pago vencido"
        : compra.estado === "CANCELADA"
          ? "Compra cancelada"
          : "Ver seguimiento";

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

        <Text style={styles.total}>
          {formatCurrency(compra.total, compra.moneda)}
        </Text>
        <Text style={[styles.status, pendiente && styles.statusPending]}>
          {getEstadoVentaLabel(compra.estado)}
        </Text>

        <Text style={styles.ctaText}>{action}</Text>
      </View>
    </Pressable>
  );
}

function PenalizacionCard({
  penalizacion,
}: {
  penalizacion: PenalizacionResponse;
}) {
  return (
    <Pressable
      style={styles.penaltyCard}
      onPress={() =>
        router.push({
          pathname:
            "/(tabs)/compras/penalizaciones/[idPenalizacion]" as any,
          params: { idPenalizacion: String(penalizacion.idPenalizacion) },
        })
      }
    >
      <Ionicons name="alert-circle-outline" size={24} color="#B45309" />
      <View style={styles.cardBody}>
        <Text style={styles.penaltyTitle}>Multa pendiente</Text>
        <Text style={styles.penaltyText}>
          Falta de respaldo al cierre de la subasta
        </Text>
        <Text style={styles.total}>
          {formatCurrency(penalizacion.importe, penalizacion.moneda)}
        </Text>
        <Text style={styles.penaltyText}>
          Generada: {formatDate(penalizacion.fechaGeneracion)}
        </Text>
        {penalizacion.idVenta ? (
          <Text style={styles.penaltyText}>Venta #{penalizacion.idVenta}</Text>
        ) : null}
        <Text style={styles.ctaText}>Pagar multa</Text>
      </View>
    </Pressable>
  );
}

export default function MisComprasScreen() {
  const { compras, loading, error, recargar } = useMisCompras();
  const {
    penalizacionesPendientes,
    loading: loadingPenalizaciones,
    recargar: recargarPenalizaciones,
  } = useMisPenalizaciones();

  useFocusEffect(
    useCallback(() => {
      void recargar();
      void recargarPenalizaciones();
    }, [recargar, recargarPenalizaciones]),
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

      {penalizacionesPendientes.length > 0 ? (
        <View style={styles.sectionBlock}>
          <Text style={styles.sectionTitle}>Pagos pendientes por multa</Text>
          {penalizacionesPendientes.map((penalizacion) => (
            <PenalizacionCard
              key={penalizacion.idPenalizacion}
              penalizacion={penalizacion}
            />
          ))}
        </View>
      ) : null}

      {(loading || loadingPenalizaciones) && compras.length === 0 ? (
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
  penaltyCard: {
    flexDirection: "row",
    gap: 12,
    backgroundColor: "#FFFBEB",
    borderWidth: 1,
    borderColor: "#FCD34D",
    borderRadius: 16,
    padding: 12,
    marginBottom: 14,
  },
  sectionBlock: { marginBottom: 8 },
  sectionTitle: {
    color: "#0F172A",
    fontSize: 16,
    fontWeight: "900",
    marginBottom: 10,
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
  penaltyTitle: { color: "#92400E", fontSize: 16, fontWeight: "900" },
  penaltyText: {
    marginTop: 4,
    color: "#78350F",
    fontSize: 12,
    fontWeight: "700",
  },
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
