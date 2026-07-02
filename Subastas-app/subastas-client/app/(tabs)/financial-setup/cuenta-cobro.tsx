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

      const response = await crearCuentaCobro({
        cbu: cbu.trim(),
        banco: banco.trim(),
        titular: titular.trim(),
      });

      const successMessage =
        (response as any)?.message ??
        "La cuenta de cobro se guardó correctamente.";

      Alert.alert("Éxito", successMessage, [
        {
          text: "OK",
          onPress: () => router.replace("/(tabs)/financial-setup" as any),
        },
      ]);
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ??
          error.response?.data?.error ??
          "No pudimos guardar la cuenta.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <View style={styles.heroCard}>
        <Text style={styles.kicker}>Medio de cobro</Text>
        <Text style={styles.title}>Cuenta bancaria</Text>
        <Text style={styles.subtitle}>
          Cargá una cuenta válida para recibir acreditaciones y cierres de
          operación.
        </Text>
      </View>

      <View style={styles.formCard}>
        <Text style={styles.label}>CBU / Alias</Text>
        <TextInput
          style={styles.input}
          placeholder="Ingresá tu CBU o alias"
          placeholderTextColor="#94A3B8"
          keyboardType="numeric"
          value={cbu}
          onChangeText={setCbu}
        />

        <Text style={styles.label}>Banco o billetera</Text>
        <TextInput
          style={styles.input}
          placeholder="Banco o billetera"
          placeholderTextColor="#94A3B8"
          value={banco}
          onChangeText={setBanco}
        />

        <Text style={styles.label}>Titular</Text>
        <TextInput
          style={styles.input}
          placeholder="Nombre del titular"
          placeholderTextColor="#94A3B8"
          value={titular}
          onChangeText={setTitular}
        />

        <View style={styles.noteBox}>
          <Text style={styles.noteTitle}>Verificación</Text>
          <Text style={styles.noteText}>
            Asegurate de que los datos coincidan con la cuenta bancaria
            asociada.
          </Text>
        </View>

        <View style={styles.actions}>
          <Pressable
            style={styles.secondaryButton}
            onPress={() => router.replace("/(tabs)/financial-setup" as any)}
          >
            <Text style={styles.secondaryButtonText}>Volver</Text>
          </Pressable>

          <Pressable
            style={styles.primaryButton}
            onPress={guardarCuenta}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="#FFFFFF" />
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
  screen: { flex: 1, backgroundColor: "#F2F5FB" },

  container: {
    paddingHorizontal: 20,
    paddingTop: 28,
    paddingBottom: 36,
  },

  heroCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    borderLeftWidth: 4,
    borderLeftColor: "#2F63F6",
    padding: 18,
    marginBottom: 16,
  },

  kicker: {
    color: "#2F63F6",
    fontSize: 12,
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 10,
  },

  title: {
    fontSize: 28,
    color: "#0F172A",
    fontWeight: "900",
    lineHeight: 34,
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#475569",
    lineHeight: 22,
  },

  formCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
    shadowColor: "#0F172A",
    shadowOpacity: 0.03,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 1,
  },

  label: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 8,
  },

  input: {
    height: 56,
    borderWidth: 1.3,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    paddingHorizontal: 14,
    marginBottom: 14,
    color: "#0F172A",
    backgroundColor: "#F8FAFC",
  },

  noteBox: {
    backgroundColor: "#F8FAFC",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 14,
    marginTop: 2,
    marginBottom: 18,
  },

  noteTitle: {
    fontSize: 12,
    color: "#0F172A",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 6,
  },

  noteText: {
    fontSize: 13,
    color: "#475569",
    lineHeight: 19,
    fontWeight: "600",
  },

  actions: {
    marginTop: 4,
    flexDirection: "row",
    gap: 12,
  },

  secondaryButton: {
    flex: 1,
    height: 52,
    borderRadius: 12,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    justifyContent: "center",
    alignItems: "center",
  },

  secondaryButtonText: {
    color: "#111827",
    fontSize: 15,
    fontWeight: "900",
  },

  primaryButton: {
    flex: 1.2,
    height: 52,
    borderRadius: 12,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
  },

  primaryButtonText: {
    color: "#FFFFFF",
    fontSize: 15,
    fontWeight: "900",
  },
});
