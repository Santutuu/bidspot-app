import { crearCuentaCobro } from "@/src/api/meAPI";
import { router } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

export default function CuentaCobroScreen() {
  const [cbu, setCbu] = useState("");
  const [banco, setBanco] = useState("");
  const [titular, setTitular] = useState("");
  const [loading, setLoading] = useState(false);

  async function guardarCuenta() {
    if (!cbu.trim() || !banco.trim() || !titular.trim()) {
      Alert.alert("Campos obligatorios", "Completá todos los campos.");
      return;
    }

    try {
      setLoading(true);

      await crearCuentaCobro({
        cbu: cbu.trim(),
        banco: banco.trim(),
        titular: titular.trim(),
      });

      Alert.alert("Cuenta registrada", "Tu cuenta de cobro fue guardada.");

      router.replace({
        pathname: "/financial-setup/medios-pago" as any,
      });
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ?? "No pudimos guardar la cuenta."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Cuenta de cobro</Text>

      <Text style={styles.title}>Cuenta bancaria</Text>

      <Text style={styles.subtitle}>
        Esta cuenta se usará para gestionar cobros y operaciones asociadas a tus
        subastas.
      </Text>

      <View style={styles.card}>
        <TextInput
          style={styles.input}
          placeholder="CBU"
          placeholderTextColor="#9CA3AF"
          keyboardType="numeric"
          value={cbu}
          onChangeText={setCbu}
        />

        <TextInput
          style={styles.input}
          placeholder="Banco o billetera"
          placeholderTextColor="#9CA3AF"
          value={banco}
          onChangeText={setBanco}
        />

        <TextInput
          style={styles.input}
          placeholder="Titular"
          placeholderTextColor="#9CA3AF"
          value={titular}
          onChangeText={setTitular}
        />

        <View style={styles.actionsRow}>
          <Pressable style={styles.secondaryButton} onPress={() => router.back()}>
            <Text style={styles.secondaryButtonText}>Cancelar</Text>
          </Pressable>

          <Pressable
            style={[styles.primaryButton, loading && styles.disabled]}
            onPress={guardarCuenta}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="white" />
            ) : (
              <Text style={styles.primaryButtonText}>Guardar</Text>
            )}
          </Pressable>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F5F6FA",
  },

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

  card: {
    backgroundColor: "white",
    borderRadius: 22,
    padding: 20,
    borderWidth: 1,
    borderColor: "#E5E7EB",
  },

  input: {
    backgroundColor: "#FAFAFA",
    borderRadius: 12,
    paddingHorizontal: 15,
    paddingVertical: 14,
    fontSize: 15,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    color: "#111827",
    height: 55,
  },

  actionsRow: {
    flexDirection: "row",
    gap: 12,
    marginTop: 8,
  },

  secondaryButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    paddingVertical: 14,
    borderRadius: 13,
    alignItems: "center",
    backgroundColor: "#FFFFFF",
  },

  primaryButton: {
    flex: 1,
    backgroundColor: "#2F63F6",
    paddingVertical: 14,
    borderRadius: 13,
    alignItems: "center",
  },

  secondaryButtonText: {
    color: "#1F2937",
    fontSize: 15,
    fontWeight: "800",
  },

  primaryButtonText: {
    color: "white",
    fontSize: 15,
    fontWeight: "800",
  },

  disabled: {
    opacity: 0.7,
  },
});