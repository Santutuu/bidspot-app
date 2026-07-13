import Ionicons from "@expo/vector-icons/Ionicons";
import { obtenerEstadoCompra } from "@/src/api/comprasAPI";
import { getApiErrorMessage } from "@/src/api/errors";
import { EstadoVenta, VentaDetalleResponse } from "@/src/dto/CompraDTO";
import { formatDate, getEstadoVentaLabel } from "@/src/utils/venta";
import { router, useFocusEffect, useLocalSearchParams, useNavigation } from "expo-router";
import { useCallback, useState } from "react";
import {
  ActivityIndicator,
  BackHandler,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

const pasosDomicilio: EstadoVenta[] = [
  "PAGO_CONFIRMADO",
  "PREPARANDO_ENVIO",
  "ENVIADO",
  "EN_CAMINO",
  "ENTREGADO",
];

const pasosRetiro: EstadoVenta[] = [
  "PAGO_CONFIRMADO",
  "PREPARANDO_RETIRO",
  "LISTO_PARA_RETIRAR",
  "RETIRADO",
];

const estadosProblematicos: EstadoVenta[] = ["INCUMPLIDA", "CANCELADA"];

export default function CompraEstadoScreen() {
  const { idVenta } = useLocalSearchParams<{ idVenta: string }>();
  const navigation = useNavigation();
  const [compra, setCompra] = useState<VentaDetalleResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    if (!idVenta) return;

    try {
      setLoading(true);
      setError(null);
      setCompra(await obtenerEstadoCompra(idVenta));
    } catch (err) {
      setError(
        getApiErrorMessage(err, "No pudimos cargar el estado de la compra."),
      );
    } finally {
      setLoading(false);
    }
  }, [idVenta]);

  useFocusEffect(
    useCallback(() => {
      void cargar();

      const onHardwareBack = () => {
        router.replace("/(tabs)/compras" as any);
        return true;
      };

      const hardwareSub = BackHandler.addEventListener(
        "hardwareBackPress",
        onHardwareBack,
      );

      const beforeRemoveSub = navigation.addListener("beforeRemove", (event) => {
        event.preventDefault();
        router.replace("/(tabs)/compras" as any);
      });

      return () => {
        hardwareSub.remove();
        beforeRemoveSub();
      };
    }, [cargar]),
  );

  if (loading && !compra) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando estado...</Text>
      </View>
    );
  }

  if (error || !compra) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>
          {error ?? "No pudimos cargar el estado."}
        </Text>
        <Pressable style={styles.primaryButton} onPress={cargar}>
          <Text style={styles.primaryButtonText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const timeline = compra.tipoEntrega === "RETIRO" ? pasosRetiro : pasosDomicilio;
  const currentIndex = timeline.findIndex((estado) => estado === compra.estado);
  const isProblemState = estadosProblematicos.includes(compra.estado);
  const pagoPendiente = compra.estado === "PENDIENTE_PAGO";

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace("/(tabs)/compras" as any)}
      >
        <Ionicons name="chevron-back" size={28} color="#0F172A" />
      </Pressable>

      <Text style={styles.title}>Estado del item</Text>
      <View style={[styles.statusCard, isProblemState && styles.problemCard]}>
        <Ionicons
          name={isProblemState ? "alert-circle-outline" : "checkmark-circle-outline"}
          size={30}
          color={isProblemState ? "#B91C1C" : "#22C55E"}
        />
        <View style={styles.statusCopy}>
          <Text style={styles.statusTitle}>{getEstadoVentaLabel(compra.estado)}</Text>
          <Text style={styles.statusSubtitle}>
            {pagoPendiente
              ? `Tenes hasta ${formatDate(compra.fechaLimitePago)} para completar el pago.`
              : isProblemState
                ? "La compra requiere revision."
                : "Te vamos a mostrar los avances a medida que se actualicen."}
          </Text>
        </View>
      </View>

      {pagoPendiente ? (
        <View style={styles.timelineCard}>
          <Text style={styles.pendingText}>Pago pendiente</Text>
        </View>
      ) : (
        <View style={styles.timelineCard}>
          {timeline.map((estado, index) => {
            const done = currentIndex >= index;
            const active = currentIndex === index;

            return (
              <View key={estado} style={styles.timelineRow}>
                <View style={styles.markerColumn}>
                  <View
                    style={[
                      styles.marker,
                      done && styles.markerDone,
                      active && styles.markerActive,
                    ]}
                  />
                  {index < timeline.length - 1 ? (
                    <View style={[styles.line, done && styles.lineDone]} />
                  ) : null}
                </View>
                <Text
                  style={[styles.timelineText, done && styles.timelineTextDone]}
                >
                  {getEstadoVentaLabel(estado)}
                </Text>
              </View>
            );
          })}
        </View>
      )}

      {compra.idFactura ? (
        <Pressable
          style={styles.primaryButton}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/compras/[idVenta]/factura" as any,
              params: { idVenta: String(compra.idVenta) },
            })
          }
        >
          <Text style={styles.primaryButtonText}>Ver factura</Text>
        </Pressable>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: { padding: 18, paddingBottom: 42 },
  backButton: {
    width: 42,
    height: 42,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 10,
  },
  title: { fontSize: 28, fontWeight: "900", color: "#0F172A" },
  statusCard: {
    marginTop: 18,
    flexDirection: "row",
    gap: 12,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 16,
  },
  problemCard: { borderColor: "#FCA5A5", backgroundColor: "#FEF2F2" },
  statusCopy: { flex: 1 },
  statusTitle: { color: "#0F172A", fontSize: 17, fontWeight: "900" },
  statusSubtitle: {
    marginTop: 5,
    color: "#64748B",
    lineHeight: 19,
    fontWeight: "700",
  },
  timelineCard: {
    marginTop: 18,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 16,
  },
  pendingText: { color: "#B45309", fontWeight: "900" },
  timelineRow: { flexDirection: "row", minHeight: 48 },
  markerColumn: { width: 28, alignItems: "center" },
  marker: {
    width: 16,
    height: 16,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: "#CBD5E1",
    backgroundColor: "#FFFFFF",
  },
  markerDone: { borderColor: "#22C55E", backgroundColor: "#DCFCE7" },
  markerActive: { borderColor: "#2F63F6" },
  line: { flex: 1, width: 2, backgroundColor: "#E2E8F0" },
  lineDone: { backgroundColor: "#86EFAC" },
  timelineText: {
    flex: 1,
    color: "#64748B",
    fontWeight: "800",
    paddingBottom: 20,
  },
  timelineTextDone: { color: "#0F172A" },
  primaryButton: {
    marginTop: 14,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingHorizontal: 18,
    paddingVertical: 12,
    alignItems: "center",
  },
  primaryButtonText: { color: "#FFFFFF", fontWeight: "900" },
  stateScreen: {
    flex: 1,
    backgroundColor: "#F2F5FB",
    alignItems: "center",
    justifyContent: "center",
    padding: 22,
  },
  stateText: { marginTop: 10, color: "#64748B", fontWeight: "700" },
  errorText: { color: "#B91C1C", fontWeight: "800", textAlign: "center" },
});
