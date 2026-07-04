import { crearCuentaCobro } from "@/src/api/meAPI";
import Ionicons from "@expo/vector-icons/Ionicons";
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

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos guardar la cuenta."
  );
}

export default function CuentaFormScreen() {
  const [cbu, setCbu] = useState("");
  const [banco, setBanco] = useState("");
  const [titular, setTitular] = useState("");
  const [loading, setLoading] = useState(false);

  function validate() {
    if (!/^\d{22}$/.test(cbu.trim())) {
      return "El CBU/CVU debe tener 22 digitos numericos.";
    }

    if (banco.trim().length < 2) return "Banco o billetera es obligatorio.";
    if (titular.trim().length < 3) return "Titular es obligatorio.";

    return null;
  }

  async function guardarCuenta() {
    const validationError = validate();

    if (validationError) {
      Alert.alert("Revisa los datos", validationError);
      return;
    }

    try {
      setLoading(true);

      await crearCuentaCobro({
        cbu: cbu.trim(),
        banco: banco.trim(),
        titular: titular.trim(),
      });

      Alert.alert("Cuenta guardada", "La cuenta de cobro quedo actualizada.", [
        {
          text: "OK",
          onPress: () =>
            router.replace("/(tabs)/financial-setup/cuenta-cobro" as any),
        },
      ]);
    } catch (error: any) {
      Alert.alert("Error", getErrorMessage(error));
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
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <Text style={styles.title}>Cuenta bancaria</Text>
      <Text style={styles.subtitle}>
        Si ya tenias una cuenta, esta nueva informacion la reemplaza.
      </Text>

      <View style={styles.formCard}>
        <Text style={styles.label}>CBU</Text>
        <TextInput
          style={styles.input}
          placeholder="22 digitos"
          placeholderTextColor="#94A3B8"
          keyboardType="numeric"
          value={cbu}
          onChangeText={setCbu}
          maxLength={22}
        />

        <Text style={styles.label}>Banco o billetera</Text>
        <TextInput
          style={styles.input}
          placeholder="Banco Galicia"
          placeholderTextColor="#94A3B8"
          value={banco}
          onChangeText={setBanco}
        />

        <Text style={styles.label}>Titular</Text>
        <TextInput
          style={styles.input}
          placeholder="Nombre y apellido"
          placeholderTextColor="#94A3B8"
          value={titular}
          onChangeText={setTitular}
        />

        <Pressable
          style={[styles.primaryButton, loading && styles.disabledButton]}
          onPress={guardarCuenta}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.primaryText}>Guardar cuenta</Text>
          )}
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  container: { paddingHorizontal: 20, paddingTop: 20, paddingBottom: 36 },
  backButton: { width: 42, height: 42, justifyContent: "center" },
  title: {
    fontSize: 28,
    fontWeight: "900",
    color: "#0F172A",
    marginTop: 10,
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 15,
    color: "#64748B",
    lineHeight: 22,
    marginBottom: 18,
  },
  formCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 22,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
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
    height: 54,
    borderWidth: 1.3,
    borderColor: "#CBD5E1",
    borderRadius: 14,
    paddingHorizontal: 14,
    marginBottom: 16,
    color: "#0F172A",
    backgroundColor: "#F8FAFC",
  },
  primaryButton: {
    marginTop: 6,
    height: 54,
    borderRadius: 14,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
  },
  disabledButton: { opacity: 0.7 },
  primaryText: { color: "#FFFFFF", fontSize: 15, fontWeight: "900" },
});
