import Ionicons from "@expo/vector-icons/Ionicons";
import { getApiErrorMessage } from "@/src/api/errors";
import { obtenerCheques, obtenerTarjetas } from "@/src/api/meAPI";
import {
  listarMisPenalizaciones,
  obtenerPenalizacion,
  pagarPenalizacion,
} from "@/src/api/penalizacionesAPI";
import { PenalizacionResponse } from "@/src/dto/CompraDTO";
import { ChequeResponseDTO } from "@/src/dto/me/ChequeDTO";
import { TarjetaResponseDTO } from "@/src/dto/me/TarjetaDTO";
import { formatCurrency, formatDate, monedasCompatibles } from "@/src/utils/venta";
import { router, useLocalSearchParams } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function PenalizacionDetalleScreen() {
  const { idPenalizacion } = useLocalSearchParams<{ idPenalizacion: string }>();
  const [penalizacion, setPenalizacion] =
    useState<PenalizacionResponse | null>(null);
  const [tarjetas, setTarjetas] = useState<TarjetaResponseDTO[]>([]);
  const [cheques, setCheques] = useState<ChequeResponseDTO[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [paying, setPaying] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    if (!idPenalizacion) return;
    try {
      setLoading(true);
      setError(null);
      const [detalle, cards, checks] = await Promise.all([
        obtenerPenalizacion(idPenalizacion),
        obtenerTarjetas(),
        obtenerCheques(),
      ]);
      setPenalizacion(detalle);
      setTarjetas(cards);
      setCheques(checks);
    } catch (err) {
      setError(getApiErrorMessage(err, "No pudimos cargar la multa."));
    } finally {
      setLoading(false);
    }
  }, [idPenalizacion]);

  useEffect(() => {
    void cargar();
  }, [cargar]);

  async function pagar() {
    if (!idPenalizacion || !penalizacion || !selectedId || paying) return;

    Alert.alert(
      "Pagar multa",
      `Se debitaran ${formatCurrency(penalizacion.importe, penalizacion.moneda)} en concepto de multa.`,
      [
        { text: "Volver", style: "cancel" },
        {
          text: "Pagar",
          onPress: async () => {
            try {
              setPaying(true);
              const updated = await pagarPenalizacion(idPenalizacion, {
                idMedioPago: selectedId,
              });
              setPenalizacion(updated);
              const pendientes = (await listarMisPenalizaciones()).filter(
                (item) => item.estado === "PENDIENTE",
              );
              Alert.alert(
                "Multa pagada",
                pendientes.length === 0
                  ? "Ya podes volver a participar en subastas."
                  : "Todavia tenes multas pendientes.",
                [
                  {
                    text: "Volver a compras",
                    onPress: () => router.replace("/(tabs)/compras" as any),
                  },
                ],
              );
            } catch (err) {
              Alert.alert(
                "No pudimos pagar la multa",
                getApiErrorMessage(err, "Intentalo nuevamente."),
              );
            } finally {
              setPaying(false);
            }
          },
        },
      ],
    );
  }

  if (loading && !penalizacion) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando multa...</Text>
      </View>
    );
  }

  if (error || !penalizacion) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>{error ?? "No pudimos cargar la multa."}</Text>
      </View>
    );
  }

  const pendiente = penalizacion.estado === "PENDIENTE";

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#0F172A" />
      </Pressable>

      <Text style={styles.title}>Multa pendiente</Text>
      <View style={styles.card}>
        <Text style={styles.reason}>Falta de respaldo al cierre de la subasta</Text>
        <Text style={styles.amount}>
          {formatCurrency(penalizacion.importe, penalizacion.moneda)}
        </Text>
        <Text style={styles.detail}>Estado: {penalizacion.estado}</Text>
        <Text style={styles.detail}>
          Generada: {formatDate(penalizacion.fechaGeneracion)}
        </Text>
        {penalizacion.fechaPago ? (
          <Text style={styles.detail}>
            Pagada: {formatDate(penalizacion.fechaPago)}
          </Text>
        ) : null}
        {penalizacion.idVenta ? (
          <Text style={styles.detail}>Venta relacionada #{penalizacion.idVenta}</Text>
        ) : null}
      </View>

      {pendiente ? (
        <>
          <Text style={styles.warning}>
            Tu cuenta no puede realizar nuevas pujas hasta que esta multa sea
            pagada.
          </Text>
          <Text style={styles.sectionTitle}>Elegir medio de pago</Text>
          {tarjetas.map((tarjeta) => {
            const id = tarjeta.idMedioPago ?? tarjeta.idTarjeta;
            const compatible = monedasCompatibles(tarjeta.moneda, penalizacion.moneda);
            return (
              <MetodoCard
                key={`tarjeta-${id}`}
                id={id}
                selected={selectedId === id}
                disabled={!compatible}
                icon="card-outline"
                title={tarjeta.numeroEnmascarado}
                detail={`${tarjeta.moneda} - ${formatCurrency(tarjeta.limiteCredito, tarjeta.moneda)}`}
                onSelect={setSelectedId}
              />
            );
          })}
          {cheques.map((cheque) => {
            const id = cheque.idMedioPago ?? cheque.idCheque;
            const compatible = monedasCompatibles(cheque.moneda, penalizacion.moneda);
            return (
              <MetodoCard
                key={`cheque-${id}`}
                id={id}
                selected={selectedId === id}
                disabled={!compatible}
                icon="document-text-outline"
                title={`Cheque #${cheque.nroCheque}`}
                detail={`${cheque.moneda} - ${formatCurrency(cheque.saldo, cheque.moneda)}`}
                onSelect={setSelectedId}
              />
            );
          })}
          <Pressable
            style={[
              styles.primaryButton,
              (!selectedId || paying) && styles.primaryButtonDisabled,
            ]}
            onPress={pagar}
            disabled={!selectedId || paying}
          >
            <Text style={styles.primaryButtonText}>
              {paying ? "Pagando..." : "Pagar multa"}
            </Text>
          </Pressable>
        </>
      ) : (
        <Text style={styles.successText}>Esta multa ya fue pagada.</Text>
      )}
    </ScrollView>
  );
}

function MetodoCard({
  id,
  selected,
  disabled,
  icon,
  title,
  detail,
  onSelect,
}: {
  id: number;
  selected: boolean;
  disabled: boolean;
  icon: keyof typeof Ionicons.glyphMap;
  title: string;
  detail: string;
  onSelect: (id: number) => void;
}) {
  return (
    <Pressable
      style={[
        styles.methodCard,
        selected && styles.methodCardSelected,
        disabled && styles.methodCardDisabled,
      ]}
      onPress={() => onSelect(id)}
      disabled={disabled}
    >
      <Ionicons name={icon} size={22} color="#1D4ED8" />
      <View style={{ flex: 1 }}>
        <Text style={styles.methodTitle}>{title}</Text>
        <Text style={styles.detail}>{detail}</Text>
        {disabled ? <Text style={styles.errorText}>Moneda incompatible</Text> : null}
      </View>
      {selected ? <Ionicons name="checkmark-circle" size={22} color="#22C55E" /> : null}
    </Pressable>
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
  card: {
    marginTop: 18,
    backgroundColor: "#FFFBEB",
    borderWidth: 1,
    borderColor: "#FCD34D",
    borderRadius: 16,
    padding: 16,
  },
  reason: { color: "#92400E", fontWeight: "900" },
  amount: {
    marginTop: 8,
    color: "#0F172A",
    fontSize: 28,
    fontWeight: "900",
  },
  detail: { marginTop: 6, color: "#475569", fontWeight: "700" },
  warning: { marginTop: 14, color: "#B45309", fontWeight: "800" },
  sectionTitle: {
    marginTop: 22,
    marginBottom: 10,
    color: "#0F172A",
    fontSize: 18,
    fontWeight: "900",
  },
  methodCard: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 14,
    marginBottom: 10,
  },
  methodCardSelected: { borderColor: "#22C55E", backgroundColor: "#F0FDF4" },
  methodCardDisabled: { opacity: 0.5 },
  methodTitle: { color: "#0F172A", fontWeight: "900" },
  primaryButton: {
    marginTop: 18,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingVertical: 15,
    alignItems: "center",
  },
  primaryButtonDisabled: { opacity: 0.55 },
  primaryButtonText: { color: "#FFFFFF", fontWeight: "900", fontSize: 15 },
  successText: { marginTop: 18, color: "#15803D", fontWeight: "900" },
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
