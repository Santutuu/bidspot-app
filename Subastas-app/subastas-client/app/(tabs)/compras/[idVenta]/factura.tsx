import Ionicons from "@expo/vector-icons/Ionicons";
import { useFacturaCompra } from "@/src/hooks/useFacturaCompra";
import { formatCurrency, formatDate } from "@/src/utils/venta";
import { router, useLocalSearchParams } from "expo-router";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function FacturaCompraScreen() {
  const { idVenta } = useLocalSearchParams<{ idVenta: string }>();
  const { factura, loading, error, recargar } = useFacturaCompra(idVenta);

  if (loading && !factura) {
    return (
      <View style={styles.stateScreen}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando factura...</Text>
      </View>
    );
  }

  if (error || !factura) {
    return (
      <View style={styles.stateScreen}>
        <Text style={styles.errorText}>
          {error ?? "La compra todavia no tiene una factura emitida."}
        </Text>
        <Pressable style={styles.primaryButton} onPress={recargar}>
          <Text style={styles.primaryButtonText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#0F172A" />
      </Pressable>

      <Text style={styles.title}>Factura</Text>
      <View style={styles.card}>
        <Text style={styles.invoiceNumber}>Factura #{factura.idFactura}</Text>
        <Text style={styles.muted}>{formatDate(factura.fechaEmision)}</Text>
        <Text style={styles.itemTitle}>{factura.tituloItem}</Text>

        <Row label="ID venta" value={`#${factura.idVenta}`} />
        <Row label="ID lote" value={`#${factura.idItemCatalogo}`} />
        <Row
          label="Puja"
          value={formatCurrency(factura.montoPuja, factura.moneda)}
        />
        <Row
          label="Comision"
          value={formatCurrency(factura.comision, factura.moneda)}
        />
        <Row
          label="Envio"
          value={formatCurrency(factura.costoEnvio, factura.moneda)}
        />
        <View style={styles.totalRow}>
          <Text style={styles.totalLabel}>Total</Text>
          <Text style={styles.totalValue}>
            {formatCurrency(factura.total, factura.moneda)}
          </Text>
        </View>
      </View>

      <Pressable
        style={styles.primaryButton}
        onPress={() =>
          router.replace({
            pathname: "/(tabs)/compras/[idVenta]" as any,
            params: { idVenta: String(factura.idVenta) },
          })
        }
      >
        <Text style={styles.primaryButtonText}>Volver al detalle</Text>
      </Pressable>
    </ScrollView>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.row}>
      <Text style={styles.rowLabel}>{label}</Text>
      <Text style={styles.rowValue}>{value}</Text>
    </View>
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
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 16,
  },
  invoiceNumber: { color: "#0F172A", fontSize: 18, fontWeight: "900" },
  muted: { marginTop: 4, color: "#64748B", fontWeight: "700" },
  itemTitle: {
    marginTop: 14,
    marginBottom: 10,
    color: "#0F172A",
    fontSize: 16,
    fontWeight: "900",
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
  },
  rowLabel: { color: "#64748B", fontWeight: "700" },
  rowValue: { color: "#0F172A", fontWeight: "900" },
  totalRow: {
    marginTop: 12,
    flexDirection: "row",
    justifyContent: "space-between",
  },
  totalLabel: { color: "#0F172A", fontSize: 18, fontWeight: "900" },
  totalValue: { color: "#0F172A", fontSize: 18, fontWeight: "900" },
  primaryButton: {
    marginTop: 18,
    backgroundColor: "#111827",
    borderRadius: 14,
    paddingVertical: 15,
    alignItems: "center",
  },
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
