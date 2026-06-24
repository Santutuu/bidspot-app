import { obtenerMediosPago } from "@/src/api/meAPI";
import { MedioPagoResponseDTO } from "@/src/dto/me/MedioPagoDTO";
import { router } from "expo-router";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function MediosPagoScreen() {
  const [medios, setMedios] = useState<MedioPagoResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  async function cargarMedios() {
    try {
      setLoading(true);
      const response = await obtenerMediosPago();
      setMedios(response);
    } catch {
      setMedios([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    cargarMedios();
  }, []);

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Medios de pago</Text>
      <Text style={styles.title}>Tus medios</Text>

      <Text style={styles.subtitle}>
        Agregá tarjetas o cheques certificados para poder participar en subastas.
      </Text>

      <Pressable
        style={styles.optionCard}
        onPress={() => router.push("/financial-setup/tarjeta")}
      >
        <Text style={styles.optionTitle}>Tarjetas +</Text>
        <Text style={styles.arrow}>→</Text>
      </Pressable>

      <Pressable
        style={styles.optionCard}
        onPress={() => router.push("/financial-setup/cheque")}
      >
        <Text style={styles.optionTitle}>Cheques +</Text>
        <Text style={styles.arrow}>→</Text>
      </Pressable>

      <Text style={styles.listTitle}>Registrados</Text>

      {loading ? (
        <ActivityIndicator color="#2F63F6" />
      ) : medios.length === 0 ? (
        <Text style={styles.emptyText}>Todavía no cargaste medios de pago.</Text>
      ) : (
        medios.map((medio) => (
          <View key={medio.idMedioPago} style={styles.paymentCard}>
            <Text style={styles.paymentType}>{medio.tipo}</Text>
            <Text style={styles.paymentDescription}>{medio.descripcion}</Text>
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F5F6FA" },

  container: {
    paddingHorizontal: 22,
    paddingTop: 34,
    paddingBottom: 42,
  },

  kicker: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "800",
    textTransform: "uppercase",
    letterSpacing: 0.6,
    marginBottom: 8,
  },

  title: {
    fontSize: 30,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 22,
  },

  optionCard: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },

  optionTitle: {
    fontSize: 18,
    color: "#111827",
    fontWeight: "900",
  },

  arrow: {
    fontSize: 26,
    color: "#2F63F6",
    fontWeight: "900",
  },

  listTitle: {
    marginTop: 14,
    marginBottom: 12,
    fontSize: 18,
    fontWeight: "900",
    color: "#111827",
  },

  emptyText: {
    color: "#6B7280",
    fontSize: 14,
  },

  paymentCard: {
    backgroundColor: "white",
    borderRadius: 16,
    padding: 15,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 10,
  },

  paymentType: {
    color: "#2F63F6",
    fontSize: 12,
    fontWeight: "900",
    marginBottom: 4,
  },

  paymentDescription: {
    color: "#111827",
    fontSize: 15,
    fontWeight: "700",
  },
});