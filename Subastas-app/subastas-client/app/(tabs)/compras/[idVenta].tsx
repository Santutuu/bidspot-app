import Ionicons from "@expo/vector-icons/Ionicons";
import { configurarEntrega } from "@/src/api/comprasAPI";
import { getApiErrorMessage } from "@/src/api/errors";
import { TipoEntrega } from "@/src/dto/CompraDTO";
import { useCompraDetalle } from "@/src/hooks/useCompraDetalle";
import {
  esCompraPendiente,
  esDireccionTecnica,
  formatCurrency,
  formatDate,
  getEstadoVentaLabel,
} from "@/src/utils/venta";
import { router, useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

const defaultImage = require("@/src/assets/images/obras_arte.jpg");

export default function CompraDetalleScreen() {
  const { idVenta } = useLocalSearchParams<{ idVenta: string }>();
  const { compra, loading, error, recargar, setCompra } =
    useCompraDetalle(idVenta);
  const [direccion, setDireccion] = useState("");
  const [savingEntrega, setSavingEntrega] = useState(false);

  useFocusEffect(
    useCallback(() => {
      void recargar();
    }, [recargar]),
  );

  useEffect(() => {
    setDireccion(compra?.direccionEntrega ?? "");
  }, [compra?.direccionEntrega]);

  async function guardarEntrega(tipoEntrega: TipoEntrega) {
    if (!idVenta || !compra) return;

    const direccionLimpia = direccion.trim();
    if (tipoEntrega === "DOMICILIO" && !direccionLimpia) {
      Alert.alert("Direccion requerida", "Ingresa una direccion de entrega.");
      return;
    }

    try {
      setSavingEntrega(true);
      const updated = await configurarEntrega(idVenta, {
        tipoEntrega,
        direccionEntrega:
          tipoEntrega === "DOMICILIO" ? direccionLimpia : null,
      });
      setCompra(updated);
    } catch (err: any) {
      Alert.alert(
        "No pudimos guardar la entrega",
        getApiErrorMessage(err, "Intentalo nuevamente."),
      );
    } finally {
      setSavingEntrega(false);
    }
  }

  function continuar() {
    if (!compra) return;

    if (!esCompraPendiente(compra.estado)) {
      router.push({
        pathname: "/(tabs)/compras/[idVenta]/estado" as any,
        params: { idVenta: String(compra.idVenta) },
      });
      return;
    }

    if (!compra.tipoEntrega) {
      Alert.alert("Entrega pendiente", "Elegi como queres recibir el item.");
      return;
    }

    if (compra.tipoEntrega === "DOMICILIO" && !compra.direccionEntrega) {
      Alert.alert("Direccion pendiente", "Guarda una direccion de entrega.");
      return;
    }

    router.push({
      pathname: "/(tabs)/compras/[idVenta]/pago" as any,
      params: { idVenta: String(compra.idVenta) },
    });
  }

  if (loading && !compra) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando compra...</Text>
      </View>
    );
  }

  if (error || !compra) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>{error ?? "No pudimos cargar la compra."}</Text>
        <Pressable style={styles.primaryButton} onPress={recargar}>
          <Text style={styles.primaryButtonText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const pendiente = compra.estado === "PENDIENTE_PAGO";
  const direccionVisible = esDireccionTecnica(compra.direccionEntrega)
    ? null
    : compra.direccionEntrega;
  const deadlineMs = compra.fechaLimitePago
    ? new Date(compra.fechaLimitePago).getTime() - Date.now()
    : null;
  const plazoVencido = pendiente && deadlineMs !== null && deadlineMs <= 0;
  const detallePlazo =
    deadlineMs !== null && deadlineMs > 0
      ? `${Math.floor(deadlineMs / 86400000)} dias ${Math.floor(
          (deadlineMs % 86400000) / 3600000,
        )} h restantes`
      : "Plazo vencido";

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#0F172A" />
      </Pressable>

      <Image
        source={compra.imagenUrl ? { uri: compra.imagenUrl } : defaultImage}
        style={styles.heroImage}
        resizeMode="cover"
      />

      <Text style={styles.title}>{compra.tituloItem}</Text>
      <Text style={[styles.status, pendiente && styles.statusPending]}>
        {getEstadoVentaLabel(compra.estado)}
      </Text>

      {pendiente ? (
        <View style={[styles.deadlineCard, plazoVencido && styles.problemCard]}>
          <Text style={styles.deadlineTitle}>
            {plazoVencido ? "Plazo vencido" : "Tenes tiempo para completar el pago"}
          </Text>
          <Text style={styles.deadlineText}>
            Hasta {formatDate(compra.fechaLimitePago)}
          </Text>
          <Text style={styles.deadlineText}>{detallePlazo}</Text>
        </View>
      ) : null}

      <View style={styles.summaryCard}>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Precio final</Text>
          <Text style={styles.summaryValue}>
            {formatCurrency(compra.montoPuja, compra.moneda)}
          </Text>
        </View>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Comision</Text>
          <Text style={styles.summaryValue}>
            {formatCurrency(compra.comision, compra.moneda)}
          </Text>
        </View>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Envio</Text>
          <Text style={styles.summaryValue}>
            {formatCurrency(compra.costoEnvio, compra.moneda)}
          </Text>
        </View>
        <View style={styles.totalRow}>
          <Text style={styles.totalLabel}>Total</Text>
          <Text style={styles.totalValue}>
            {formatCurrency(compra.total, compra.moneda)}
          </Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Fechas</Text>
        <Text style={styles.paymentText}>
          Adjudicacion: {formatDate(compra.fechaVenta)}
        </Text>
        <Text style={styles.paymentText}>
          Limite de pago: {formatDate(compra.fechaLimitePago)}
        </Text>
        {compra.fechaPagoConfirmado ? (
          <Text style={styles.paymentText}>
            Pago confirmado: {formatDate(compra.fechaPagoConfirmado)}
          </Text>
        ) : null}
        {compra.fechaIncumplimiento ? (
          <Text style={styles.paymentText}>
            Incumplimiento: {formatDate(compra.fechaIncumplimiento)}
          </Text>
        ) : null}
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Entrega</Text>

        <Pressable
          style={[
            styles.choiceRow,
            compra.tipoEntrega === "DOMICILIO" && styles.choiceRowActive,
          ]}
          onPress={() => guardarEntrega("DOMICILIO")}
          disabled={savingEntrega}
        >
          <Ionicons name="home-outline" size={20} color="#0F172A" />
          <View style={styles.choiceCopy}>
            <Text style={styles.choiceTitle}>Enviar a domicilio</Text>
            <Text style={styles.choiceSubtitle}>Entrega estandar (3-5 dias)</Text>
          </View>
        </Pressable>

        {compra.tipoEntrega === "DOMICILIO" || !compra.tipoEntrega ? (
          <View style={styles.addressBlock}>
            <TextInput
              style={styles.input}
              placeholder="Direccion de entrega"
              placeholderTextColor="#94A3B8"
              value={direccion}
              onChangeText={setDireccion}
            />
            <Pressable
              style={styles.secondaryButton}
              onPress={() => guardarEntrega("DOMICILIO")}
              disabled={savingEntrega}
            >
              <Text style={styles.secondaryButtonText}>
                {savingEntrega ? "Guardando..." : "Guardar direccion"}
              </Text>
            </Pressable>
          </View>
        ) : null}

        <Pressable
          style={[
            styles.choiceRow,
            compra.tipoEntrega === "RETIRO" && styles.choiceRowActive,
          ]}
          onPress={() => guardarEntrega("RETIRO")}
          disabled={savingEntrega}
        >
          <Ionicons name="location-outline" size={20} color="#0F172A" />
          <View style={styles.choiceCopy}>
            <Text style={styles.choiceTitle}>Retirar personalmente</Text>
            <Text style={styles.choiceSubtitle}>
              Retiro en ubicacion de la subasta. Sin costo de envio.
            </Text>
            {compra.ubicacionRetiro ? (
              <Text style={styles.pickupText}>{compra.ubicacionRetiro}</Text>
            ) : null}
          </View>
        </Pressable>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Metodo de pago</Text>
        <Text style={styles.paymentText}>
          {compra.idMedioPago
            ? `Medio seleccionado #${compra.idMedioPago}`
            : "Todavia no elegiste un medio de pago."}
        </Text>
      </View>

      {compra.tipoEntrega === "DOMICILIO" && direccionVisible ? (
        <Text style={styles.deliveryText}>Direccion: {direccionVisible}</Text>
      ) : null}

      {compra.idFactura ? (
        <Pressable
          style={styles.secondaryWideButton}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/compras/[idVenta]/factura" as any,
              params: { idVenta: String(compra.idVenta) },
            })
          }
        >
          <Text style={styles.secondaryWideText}>Ver factura</Text>
        </Pressable>
      ) : null}

      <Pressable
        style={[styles.primaryButton, plazoVencido && styles.primaryButtonDisabled]}
        onPress={continuar}
        disabled={plazoVencido}
      >
        <Text style={styles.primaryButtonText}>
          {plazoVencido
            ? "Pago vencido"
            : pendiente
              ? "Continuar con el pago"
              : "Ver estado del item"}
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
  heroImage: {
    width: "100%",
    height: 190,
    borderRadius: 18,
    backgroundColor: "#E2E8F0",
    marginBottom: 16,
  },
  title: {
    fontSize: 26,
    lineHeight: 32,
    fontWeight: "900",
    color: "#0F172A",
  },
  status: {
    marginTop: 8,
    fontSize: 13,
    color: "#475569",
    fontWeight: "900",
  },
  statusPending: { color: "#B45309" },
  deadlineCard: {
    marginTop: 14,
    backgroundColor: "#FFFBEB",
    borderWidth: 1,
    borderColor: "#FCD34D",
    borderRadius: 16,
    padding: 14,
  },
  problemCard: { borderColor: "#FCA5A5", backgroundColor: "#FEF2F2" },
  deadlineTitle: { color: "#92400E", fontWeight: "900" },
  deadlineText: { marginTop: 4, color: "#78350F", fontWeight: "700" },
  summaryCard: {
    marginTop: 18,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 16,
  },
  summaryRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 12,
  },
  summaryLabel: { color: "#64748B", fontWeight: "700" },
  summaryValue: { color: "#0F172A", fontWeight: "900" },
  totalRow: {
    borderTopWidth: 1,
    borderTopColor: "#E2E8F0",
    paddingTop: 14,
    flexDirection: "row",
    justifyContent: "space-between",
  },
  totalLabel: { fontSize: 17, color: "#0F172A", fontWeight: "900" },
  totalValue: { fontSize: 18, color: "#0F172A", fontWeight: "900" },
  section: {
    marginTop: 16,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 14,
  },
  sectionTitle: {
    fontSize: 16,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 12,
  },
  choiceRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 10,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 14,
    padding: 12,
    marginBottom: 10,
  },
  choiceRowActive: {
    borderColor: "#22C55E",
    backgroundColor: "#F0FDF4",
  },
  choiceCopy: { flex: 1 },
  choiceTitle: { color: "#0F172A", fontWeight: "900" },
  choiceSubtitle: {
    marginTop: 3,
    color: "#64748B",
    fontSize: 12,
    fontWeight: "700",
    lineHeight: 17,
  },
  pickupText: {
    marginTop: 6,
    color: "#0F172A",
    fontSize: 12,
    fontWeight: "800",
  },
  addressBlock: { marginBottom: 10 },
  input: {
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    paddingHorizontal: 12,
    paddingVertical: 12,
    fontSize: 15,
    color: "#0F172A",
    backgroundColor: "#FFFFFF",
  },
  paymentText: { color: "#475569", fontWeight: "700" },
  deliveryText: {
    marginTop: 10,
    color: "#475569",
    fontWeight: "800",
    textAlign: "center",
  },
  primaryButton: {
    marginTop: 18,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingVertical: 15,
    alignItems: "center",
  },
  primaryButtonText: { color: "#FFFFFF", fontWeight: "900", fontSize: 15 },
  primaryButtonDisabled: { opacity: 0.5 },
  secondaryWideButton: {
    marginTop: 14,
    backgroundColor: "#EFF6FF",
    borderRadius: 14,
    paddingVertical: 13,
    alignItems: "center",
  },
  secondaryWideText: { color: "#2563EB", fontWeight: "900" },
  secondaryButton: {
    marginTop: 8,
    alignSelf: "flex-start",
    backgroundColor: "#EFF6FF",
    borderRadius: 12,
    paddingHorizontal: 12,
    paddingVertical: 9,
  },
  secondaryButtonText: { color: "#2563EB", fontWeight: "900" },
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
