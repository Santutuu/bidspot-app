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
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace("/(tabs)/financial-setup" as any)}
      >
        <Text style={styles.backText}>‹</Text>
      </Pressable>

      <Text style={styles.title}>Cuenta bancaria</Text>

      <Text style={styles.label}>CBU</Text>
      <TextInput
        style={styles.input}
        placeholder="Ingresá tu CBU"
        placeholderTextColor="#9CA3AF"
        keyboardType="numeric"
        value={cbu}
        onChangeText={setCbu}
      />

      <Text style={styles.label}>Banco o billetera</Text>
      <TextInput
        style={styles.input}
        placeholder="Banco o billetera"
        placeholderTextColor="#9CA3AF"
        value={banco}
        onChangeText={setBanco}
      />

      <Text style={styles.label}>Titular</Text>
      <TextInput
        style={styles.input}
        placeholder="Titular"
        placeholderTextColor="#9CA3AF"
        value={titular}
        onChangeText={setTitular}
      />

      <View style={styles.actions}>
        <Pressable
          style={styles.cancelButton}
          onPress={() => router.replace("/(tabs)/financial-setup" as any)}
        >
          <Text style={styles.cancelIcon}>×</Text>
        </Pressable>

        <Pressable
          style={styles.confirmButton}
          onPress={guardarCuenta}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#2F63F6" />
          ) : (
            <Text style={styles.confirmIcon}>✓</Text>
          )}
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#FFFFFF" },

  container: {
    paddingHorizontal: 24,
    paddingTop: 24,
    paddingBottom: 42,
  },

  backButton: {
    width: 40,
    height: 40,
    justifyContent: "center",
    marginBottom: 10,
  },

  backText: {
    fontSize: 38,
    color: "#111827",
  },

  title: {
    fontSize: 25,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 28,
  },

  label: {
    color: "#6B7280",
    fontSize: 12,
    fontWeight: "900",
    marginBottom: 6,
  },

  input: {
    height: 48,
    borderWidth: 1.3,
    borderColor: "#111827",
    borderRadius: 0,
    paddingHorizontal: 12,
    marginBottom: 14,
    color: "#111827",
    backgroundColor: "#FFFFFF",
  },

  actions: {
    marginTop: 48,
    flexDirection: "row",
    justifyContent: "space-between",
  },

  cancelButton: {
    width: 58,
    height: 58,
    justifyContent: "center",
    alignItems: "center",
  },

  confirmButton: {
    width: 58,
    height: 58,
    borderWidth: 1.5,
    borderColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
  },

  cancelIcon: {
    fontSize: 52,
    color: "#111827",
    fontWeight: "200",
  },

  confirmIcon: {
    fontSize: 34,
    color: "#2F63F6",
    fontWeight: "700",
  },
});
