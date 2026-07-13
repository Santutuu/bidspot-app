import Ionicons from "@expo/vector-icons/Ionicons";
import { useUltimaAdjudicacion } from "@/src/hooks/useUltimaAdjudicacion";
import {
  formatCurrency,
  formatDate,
  getEstadoVentaLabel,
} from "@/src/utils/venta";
import { router, useFocusEffect } from "expo-router";
import { useCallback } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function MensajeriaScreen() {
  const { adjudicacion, loading, error, recargar } = useUltimaAdjudicacion();

  useFocusEffect(
    useCallback(() => {
      void recargar();
    }, [recargar]),
  );

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerCard}>
        <View style={styles.avatarWrap}>
          <Ionicons name="business" size={18} color="#1D4ED8" />
        </View>
        <View style={styles.headerMeta}>
          <Text style={styles.headerTitle}>Administrador Bidmax</Text>
          <Text style={styles.headerSubtitle}>Mensajes de la empresa</Text>
        </View>
      </View>

      {loading && !adjudicacion ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.stateText}>Cargando adjudicacion...</Text>
        </View>
      ) : null}

      {error ? (
        <View style={styles.stateCard}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}

      {adjudicacion && !adjudicacion.tieneAdjudicacion ? (
        <View style={styles.stateCard}>
          <Ionicons name="mail-open-outline" size={28} color="#64748B" />
          <Text style={styles.stateText}>No tenes adjudicaciones recientes.</Text>
        </View>
      ) : null}

      {adjudicacion?.tieneAdjudicacion && adjudicacion.idVenta ? (
        <Pressable
          style={styles.messageRowSupport}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/compras/[idVenta]" as any,
              params: { idVenta: String(adjudicacion.idVenta) },
            })
          }
        >
          <View style={styles.messageMetaWrap}>
            <View style={styles.inlineAvatar}>
              <Ionicons name="business-outline" size={15} color="#1D4ED8" />
            </View>
          </View>
          <View style={styles.bubbleSupportStrong}>
            <Text style={styles.messageTitle}>Adjudicacion confirmada</Text>
            <Text style={styles.messageItem}>{adjudicacion.tituloItem}</Text>

            <View style={styles.amountPanel}>
              <View style={styles.amountRow}>
                <Text style={styles.amountLabel}>Puja</Text>
                <Text style={styles.amountValue}>
                  {formatCurrency(
                    adjudicacion.montoPuja ?? 0,
                    adjudicacion.moneda ?? "PESOS",
                  )}
                </Text>
              </View>
              <View style={styles.amountRow}>
                <Text style={styles.amountLabel}>Comision</Text>
                <Text style={styles.amountValue}>
                  {formatCurrency(
                    adjudicacion.comision ?? 0,
                    adjudicacion.moneda ?? "PESOS",
                  )}
                </Text>
              </View>
              <View style={styles.amountRow}>
                <Text style={styles.amountLabel}>Envio</Text>
                <Text style={styles.amountValue}>
                  {formatCurrency(
                    adjudicacion.costoEnvio ?? 0,
                    adjudicacion.moneda ?? "PESOS",
                  )}
                </Text>
              </View>
              <View style={styles.totalRow}>
                <Text style={styles.totalLabel}>Total</Text>
                <Text style={styles.total}>
                  {formatCurrency(
                    adjudicacion.total ?? 0,
                    adjudicacion.moneda ?? "PESOS",
                  )}
                </Text>
              </View>
            </View>

            <View style={styles.metaRow}>
              <Ionicons name="time-outline" size={15} color="#64748B" />
              <Text style={styles.line}>
                Limite: {formatDate(adjudicacion.fechaLimitePago)}
              </Text>
            </View>
            {adjudicacion.estado ? (
              <View style={styles.statusPill}>
                <Text style={styles.status}>
                  {getEstadoVentaLabel(adjudicacion.estado)}
                </Text>
              </View>
            ) : null}
            <Text style={styles.actionLine}>Ver detalle de compra</Text>
          </View>
        </Pressable>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F1F5F9" },
  content: { padding: 16, paddingBottom: 32 },
  headerCard: {
    marginBottom: 14,
    backgroundColor: "#FFFFFF",
    borderRadius: 16,
    paddingHorizontal: 14,
    paddingVertical: 12,
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  avatarWrap: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "#DBEAFE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  headerMeta: { flex: 1 },
  headerTitle: { color: "#0F172A", fontSize: 15, fontWeight: "800" },
  headerSubtitle: {
    marginTop: 2,
    color: "#64748B",
    fontSize: 12,
    fontWeight: "600",
  },
  messageMetaWrap: {
    width: 28,
    alignItems: "center",
    marginRight: 6,
  },
  inlineAvatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#BFDBFE",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#EFF6FF",
  },
  messageRowSupport: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 10,
    width: "100%",
  },
  bubbleSupportStrong: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 14,
    borderTopLeftRadius: 6,
    paddingHorizontal: 13,
    paddingVertical: 11,
  },
  messageTitle: {
    color: "#475569",
    fontSize: 12,
    fontWeight: "900",
    textTransform: "uppercase",
  },
  messageItem: {
    marginTop: 7,
    color: "#0F172A",
    fontSize: 16,
    lineHeight: 22,
    fontWeight: "900",
  },
  amountPanel: {
    marginTop: 11,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 10,
    paddingHorizontal: 10,
    paddingVertical: 8,
  },
  amountRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
    marginBottom: 8,
  },
  amountLabel: { color: "#475569", fontSize: 13, fontWeight: "800" },
  amountValue: { color: "#0F172A", fontSize: 13, fontWeight: "900" },
  totalRow: {
    borderTopWidth: 1,
    borderTopColor: "#E2E8F0",
    paddingTop: 8,
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
  },
  totalLabel: { color: "#0F172A", fontSize: 14, fontWeight: "900" },
  total: { color: "#0F172A", fontSize: 16, fontWeight: "900" },
  metaRow: {
    marginTop: 10,
    flexDirection: "row",
    alignItems: "center",
    gap: 5,
  },
  line: { color: "#475569", fontSize: 12, fontWeight: "700" },
  statusPill: {
    alignSelf: "flex-start",
    marginTop: 9,
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 999,
    paddingHorizontal: 9,
    paddingVertical: 4,
  },
  status: { color: "#475569", fontSize: 12, fontWeight: "900" },
  actionLine: { marginTop: 12, color: "#1D4ED8", fontSize: 13, fontWeight: "900" },
  stateCard: {
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderRadius: 16,
    padding: 20,
  },
  stateText: { marginTop: 8, color: "#64748B", fontWeight: "700" },
  errorText: { color: "#B91C1C", fontWeight: "800", textAlign: "center" },
});
