import Ionicons from "@expo/vector-icons/Ionicons";
import {
  confirmarCompra,
  seleccionarMedioPago,
} from "@/src/api/comprasAPI";
import { getApiErrorMessage } from "@/src/api/errors";
import { obtenerCheques, obtenerTarjetas } from "@/src/api/meAPI";
import { ChequeResponseDTO } from "@/src/dto/me/ChequeDTO";
import { TarjetaResponseDTO } from "@/src/dto/me/TarjetaDTO";
import { useCompraDetalle } from "@/src/hooks/useCompraDetalle";
import { formatCurrency, monedasCompatibles } from "@/src/utils/venta";
import { router, useLocalSearchParams } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

type PaymentOption = {
  kind: "TARJETA" | "CHEQUE";
  id: number;
  label: string;
  detail: string;
  moneda: string;
};

function getTarjetaId(tarjeta: TarjetaResponseDTO) {
  return tarjeta.idMedioPago ?? tarjeta.idTarjeta;
}

function getChequeId(cheque: ChequeResponseDTO) {
  return cheque.idMedioPago ?? cheque.idCheque;
}

export default function CompraPagoScreen() {
  const { idVenta } = useLocalSearchParams<{ idVenta: string }>();
  const { compra, loading, error, setCompra } = useCompraDetalle(idVenta);
  const [tarjetas, setTarjetas] = useState<TarjetaResponseDTO[]>([]);
  const [cheques, setCheques] = useState<ChequeResponseDTO[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [loadingMethods, setLoadingMethods] = useState(true);
  const [savingMethod, setSavingMethod] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    async function cargarMetodos() {
      try {
        setLoadingMethods(true);
        const [cards, checks] = await Promise.all([
          obtenerTarjetas(),
          obtenerCheques(),
        ]);
        setTarjetas(cards);
        setCheques(checks);
      } catch (err) {
        Alert.alert(
          "Medios de pago",
          getApiErrorMessage(err, "No pudimos cargar tus medios de pago."),
        );
      } finally {
        setLoadingMethods(false);
      }
    }

    void cargarMetodos();
  }, []);

  useEffect(() => {
    if (compra?.idMedioPago) setSelectedId(compra.idMedioPago);
  }, [compra?.idMedioPago]);

  const options = useMemo<PaymentOption[]>(() => {
    const cardOptions = tarjetas.map((tarjeta) => ({
      kind: "TARJETA" as const,
      id: getTarjetaId(tarjeta),
      label: tarjeta.numeroEnmascarado,
      detail: `${tarjeta.nombre} - vence ${tarjeta.fechaVto}`,
      moneda: tarjeta.moneda,
    }));

    const chequeOptions = cheques.map((cheque) => ({
      kind: "CHEQUE" as const,
      id: getChequeId(cheque),
      label: `Cheque #${cheque.nroCheque}`,
      detail: cheque.beneficiario,
      moneda: cheque.moneda,
    }));

    return [...cardOptions, ...chequeOptions];
  }, [cheques, tarjetas]);

  const selectedOption = options.find((option) => option.id === selectedId);

  async function guardarMetodo(idMedioPago: number) {
    if (!idVenta || savingMethod) return;

    try {
      setSavingMethod(true);
      setSelectedId(idMedioPago);
      const updated = await seleccionarMedioPago(idVenta, { idMedioPago });
      setCompra(updated);
    } catch (err) {
      Alert.alert(
        "Metodo de pago",
        getApiErrorMessage(err, "No pudimos seleccionar este metodo."),
      );
    } finally {
      setSavingMethod(false);
    }
  }

  async function confirmar() {
    if (!idVenta || !compra || submitting) return;

    const medioPagoId = selectedId ?? compra.idMedioPago;
    if (!compra.tipoEntrega) {
      Alert.alert("Entrega pendiente", "Primero elegi una forma de entrega.");
      return;
    }

    if (compra.tipoEntrega === "DOMICILIO" && !compra.direccionEntrega) {
      Alert.alert("Direccion pendiente", "Ingresa una direccion de entrega.");
      return;
    }

    if (!medioPagoId) {
      Alert.alert("Metodo de pago pendiente", "Elegi una tarjeta o cheque.");
      return;
    }

    const tipoEntrega = compra.tipoEntrega;

    Alert.alert(
      "Confirmar compra",
      `Se debitaran ${formatCurrency(compra.total, compra.moneda)} del medio seleccionado.`,
      [
        { text: "Volver", style: "cancel" },
        {
          text: "Confirmar compra",
          onPress: async () => {
            try {
              setSubmitting(true);
              const updated = await confirmarCompra(idVenta, {
                idMedioPago: medioPagoId,
                tipoEntrega,
                direccionEntrega:
                  tipoEntrega === "DOMICILIO"
                    ? compra.direccionEntrega
                    : null,
              });
              setCompra(updated);
              Alert.alert("Pago confirmado con exito", undefined, [
                {
                  text: "Ver estado item",
                  onPress: () =>
                    router.replace({
                      pathname: "/(tabs)/compras/[idVenta]/estado" as any,
                      params: { idVenta: String(idVenta) },
                    }),
                },
              ]);
            } catch (err) {
              Alert.alert(
                "No pudimos confirmar la compra",
                getApiErrorMessage(err, "Intentalo nuevamente."),
              );
            } finally {
              setSubmitting(false);
            }
          },
        },
      ],
    );
  }

  if (loading && !compra) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando pago...</Text>
      </View>
    );
  }

  if (error || !compra) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>
          {error ?? "No pudimos cargar el pago."}
        </Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#0F172A" />
      </Pressable>

      <Text style={styles.title}>Confirmar compra</Text>
      <Text style={styles.subtitle}>{compra.tituloItem}</Text>

      <View style={styles.summaryCard}>
        <Text style={styles.totalLabel}>Total a pagar</Text>
        <Text style={styles.totalValue}>
          {formatCurrency(compra.total, compra.moneda)}
        </Text>
        <Text style={styles.breakdownLine}>
          Puja: {formatCurrency(compra.montoPuja, compra.moneda)}
        </Text>
        <Text style={styles.breakdownLine}>
          Comision: {formatCurrency(compra.comision, compra.moneda)}
        </Text>
        <Text style={styles.breakdownLine}>
          Envio: {formatCurrency(compra.costoEnvio, compra.moneda)}
        </Text>
        <Text style={styles.summaryText}>
          {compra.tipoEntrega === "DOMICILIO"
            ? `Enviar a ${compra.direccionEntrega ?? "direccion pendiente"}`
            : `Retirar en ${compra.ubicacionRetiro ?? "ubicacion de la subasta"}`}
        </Text>
      </View>

      <Text style={styles.sectionTitle}>Tarjetas</Text>
      {loadingMethods ? <ActivityIndicator color="#2F63F6" /> : null}
      {!loadingMethods && tarjetas.length === 0 ? (
        <Text style={styles.emptyText}>No tenes tarjetas cargadas.</Text>
      ) : null}
      {tarjetas.map((tarjeta) => {
        const id = getTarjetaId(tarjeta);
        const selected = selectedId === id;
        const compatible = monedasCompatibles(tarjeta.moneda, compra.moneda);
        return (
          <Pressable
            key={`tarjeta-${id}`}
            style={[
              styles.methodCard,
              selected && styles.methodCardSelected,
              !compatible && styles.methodCardDisabled,
            ]}
            onPress={() => guardarMetodo(id)}
            disabled={!compatible || savingMethod}
          >
            <Ionicons name="card-outline" size={24} color="#1D4ED8" />
            <View style={styles.methodCopy}>
              <Text style={styles.methodTitle}>{tarjeta.numeroEnmascarado}</Text>
              <Text style={styles.methodDetail}>
                {tarjeta.nombre} - vence {tarjeta.fechaVto} -{" "}
                {formatCurrency(tarjeta.limiteCredito, tarjeta.moneda)}
              </Text>
              {!compatible ? (
                <Text style={styles.incompatibleText}>Moneda incompatible</Text>
              ) : null}
            </View>
            {selected ? (
              <Ionicons name="checkmark-circle" size={22} color="#22C55E" />
            ) : null}
          </Pressable>
        );
      })}

      <Text style={styles.sectionTitle}>Cheques</Text>
      {!loadingMethods && cheques.length === 0 ? (
        <Text style={styles.emptyText}>No tenes cheques cargados.</Text>
      ) : null}
      {cheques.map((cheque) => {
        const id = getChequeId(cheque);
        const selected = selectedId === id;
        const compatible = monedasCompatibles(cheque.moneda, compra.moneda);
        return (
          <Pressable
            key={`cheque-${id}`}
            style={[
              styles.methodCard,
              selected && styles.methodCardSelected,
              !compatible && styles.methodCardDisabled,
            ]}
            onPress={() => guardarMetodo(id)}
            disabled={!compatible || savingMethod}
          >
            <Ionicons name="document-text-outline" size={24} color="#0F172A" />
            <View style={styles.methodCopy}>
              <Text style={styles.methodTitle}>Cheque #{cheque.nroCheque}</Text>
              <Text style={styles.methodDetail}>
                {cheque.beneficiario} - {formatCurrency(cheque.saldo, cheque.moneda)}
              </Text>
              {!compatible ? (
                <Text style={styles.incompatibleText}>Moneda incompatible</Text>
              ) : null}
            </View>
            {selected ? (
              <Ionicons name="checkmark-circle" size={22} color="#22C55E" />
            ) : null}
          </Pressable>
        );
      })}

      <View style={styles.confirmCard}>
        <Text style={styles.confirmLabel}>Metodo seleccionado</Text>
        <Text style={styles.confirmValue}>
          {selectedOption
            ? `${selectedOption.label} - ${selectedOption.detail}`
            : "Sin seleccionar"}
        </Text>
      </View>

      <Pressable
        style={[
          styles.primaryButton,
          (submitting || savingMethod) && styles.primaryButtonDisabled,
        ]}
        onPress={confirmar}
        disabled={submitting || savingMethod}
      >
        <Text style={styles.primaryButtonText}>
          {submitting ? "Confirmando..." : "Confirmar compra"}
        </Text>
      </Pressable>
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
  subtitle: {
    marginTop: 6,
    color: "#475569",
    fontWeight: "800",
    fontSize: 15,
  },
  summaryCard: {
    marginTop: 18,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 16,
  },
  totalLabel: { color: "#64748B", fontWeight: "800" },
  totalValue: {
    marginTop: 4,
    marginBottom: 10,
    fontSize: 30,
    color: "#0F172A",
    fontWeight: "900",
  },
  breakdownLine: { color: "#334155", fontWeight: "800", marginBottom: 6 },
  summaryText: {
    marginTop: 10,
    color: "#334155",
    lineHeight: 20,
    fontWeight: "700",
  },
  sectionTitle: {
    marginTop: 22,
    marginBottom: 10,
    fontSize: 18,
    color: "#0F172A",
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
  methodCardSelected: {
    borderColor: "#22C55E",
    backgroundColor: "#F0FDF4",
  },
  methodCardDisabled: { opacity: 0.5 },
  methodCopy: { flex: 1 },
  methodTitle: { color: "#0F172A", fontSize: 16, fontWeight: "900" },
  methodDetail: {
    marginTop: 4,
    color: "#64748B",
    fontSize: 12,
    fontWeight: "700",
  },
  incompatibleText: {
    marginTop: 4,
    color: "#B91C1C",
    fontSize: 12,
    fontWeight: "800",
  },
  emptyText: { color: "#64748B", fontWeight: "700", marginBottom: 10 },
  confirmCard: {
    marginTop: 16,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 14,
  },
  confirmLabel: { color: "#64748B", fontWeight: "800" },
  confirmValue: { marginTop: 4, color: "#0F172A", fontWeight: "900" },
  primaryButton: {
    marginTop: 18,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingVertical: 15,
    alignItems: "center",
  },
  primaryButtonDisabled: { opacity: 0.55 },
  primaryButtonText: { color: "#FFFFFF", fontWeight: "900", fontSize: 15 },
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
