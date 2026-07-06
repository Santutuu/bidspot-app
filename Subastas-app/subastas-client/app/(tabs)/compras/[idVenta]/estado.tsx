import Ionicons from "@expo/vector-icons/Ionicons";
import { obtenerEstadoCompra } from "@/src/api/comprasAPI";
import { CompraEstadoResponse, EstadoVenta } from "@/src/dto/CompraDTO";
import { router, useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

const timeline: Array<{ estado: EstadoVenta; label: string }> = [
  { estado: "PENDIENTE_PAGO", label: "Pago pendiente" },
  { estado: "PAGO_CONFIRMADO", label: "Pago confirmado" },
  { estado: "PREPARANDO_ENVIO", label: "Preparando envio" },
  { estado: "ENVIADO", label: "Enviado" },
  { estado: "EN_CAMINO", label: "En camino" },
  { estado: "ENTREGADO", label: "Entregado" },
];

function estadoLabel(estado: EstadoVenta) {
  if (estado === "MULTADA") return "Compra multada";
  if (estado === "INCUMPLIDA") return "Compra incumplida";
  if (estado === "CANCELADA") return "Compra cancelada";

  return timeline.find((step) => step.estado === estado)?.label ?? estado;
}

function stepIndex(estado: EstadoVenta) {
  return timeline.findIndex((step) => step.estado === estado);
}

export default function CompraEstadoScreen() {
  const { idVenta } = useLocalSearchParams<{ idVenta: string }>();
  const [estado, setEstado] = useState<CompraEstadoResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    if (!idVenta) return;

    try {
      setLoading(true);
      setError(null);
      setEstado(await obtenerEstadoCompra(idVenta));
    } catch (err: any) {
      setError(
        err.response?.data?.message ??
          err.response?.data?.error ??
          "No pudimos cargar el estado de la compra.",
      );
    } finally {
      setLoading(false);
    }
  }, [idVenta]);

  useFocusEffect(
    useCallback(() => {
      void cargar();
    }, [cargar]),
  );

  if (loading && !estado) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando estado...</Text>
      </View>
    );
  }

  if (error || !estado) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>{error ?? "No pudimos cargar el estado."}</Text>
        <Pressable style={styles.primaryButton} onPress={cargar}>
          <Text style={styles.primaryButtonText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const currentIndex = stepIndex(estado.estado);
  const isProblemState = ["MULTADA", "INCUMPLIDA", "CANCELADA"].includes(
    estado.estado,
  );

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
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
          <Text style={styles.statusTitle}>{estadoLabel(estado.estado)}</Text>
          <Text style={styles.statusSubtitle}>
            {isProblemState
              ? "La compra requiere revision. No se implementa logica de multa desde la app."
              : "Te vamos a mostrar los avances de entrega a medida que se actualicen."}
          </Text>
        </View>
      </View>

      <View style={styles.timelineCard}>
        {timeline.map((step, index) => {
          const done = currentIndex >= index;
          const active = currentIndex === index;

          return (
            <View key={step.estado} style={styles.timelineRow}>
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
              <Text style={[styles.timelineText, done && styles.timelineTextDone]}>
                {step.label}
              </Text>
            </View>
          );
        })}
      </View>

      {estado.entregaEstimada ? (
        <Text style={styles.estimatedText}>
          Entrega estimada: {estado.entregaEstimada}
        </Text>
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
  estimatedText: {
    marginTop: 14,
    color: "#475569",
    fontWeight: "800",
    textAlign: "center",
  },
  primaryButton: {
    marginTop: 14,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingHorizontal: 18,
    paddingVertical: 12,
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
