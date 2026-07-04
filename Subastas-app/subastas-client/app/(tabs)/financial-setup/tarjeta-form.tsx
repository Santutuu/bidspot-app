import { crearTarjeta } from "@/src/api/meAPI";
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
    "No pudimos guardar la tarjeta."
  );
}

export default function TarjetaFormScreen() {
  const [numero, setNumero] = useState("");
  const [nombre, setNombre] = useState("");
  const [fechaVto, setFechaVto] = useState("");
  const [cvv, setCvv] = useState("");
  const [loading, setLoading] = useState(false);

  function handleNumeroChange(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 19);
    setNumero(digits);
  }

  function handleFechaVtoChange(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 4);

    if (digits.length <= 2) {
      setFechaVto(digits);
      return;
    }

    setFechaVto(`${digits.slice(0, 2)}/${digits.slice(2)}`);
  }

  function handleCvvChange(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 4);
    setCvv(digits);
  }

  function validate() {
    const digits = numero.replace(/\D/g, "");

    if (!/^\d{13,19}$/.test(digits)) {
      return "El numero de tarjeta debe tener entre 13 y 19 digitos.";
    }

    if (nombre.trim().length < 3)
      return "El nombre del titular es obligatorio.";
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(fechaVto.trim())) {
      return "La fecha de vencimiento debe tener formato MM/AA.";
    }
    if (!/^\d{3,4}$/.test(cvv.trim()))
      return "El CVV debe tener 3 o 4 digitos.";

    return null;
  }

  async function guardarTarjeta() {
    const validationError = validate();

    if (validationError) {
      Alert.alert("Revisa los datos", validationError);
      return;
    }

    try {
      setLoading(true);

      await crearTarjeta({
        numero: numero.replace(/\D/g, ""),
        nombre: nombre.trim(),
        fechaVto: fechaVto.trim(),
        cvv: cvv.trim(),
      });

      Alert.alert("Tarjeta guardada", "La tarjeta se agrego correctamente.", [
        {
          text: "OK",
          onPress: () =>
            router.replace("/(tabs)/financial-setup/tarjeta" as any),
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
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <Text style={styles.title}>Agregar tarjeta</Text>
      <Text style={styles.subtitle}>
        Carga una tarjeta valida como medio de pago.
      </Text>

      <View style={styles.formCard}>
        <Text style={styles.label}>Numero tarjeta</Text>
        <TextInput
          style={styles.input}
          placeholder="Sin espacios"
          placeholderTextColor="#94A3B8"
          keyboardType="number-pad"
          value={numero}
          onChangeText={handleNumeroChange}
          maxLength={19}
        />

        <Text style={styles.label}>Nombre</Text>
        <TextInput
          style={styles.input}
          placeholder="Como figura en la tarjeta"
          placeholderTextColor="#94A3B8"
          value={nombre}
          onChangeText={setNombre}
        />

        <View style={styles.inlineRow}>
          <View style={styles.inlineField}>
            <Text style={styles.label}>Fecha vencimiento</Text>
            <TextInput
              style={styles.input}
              placeholder="MM/AA"
              placeholderTextColor="#94A3B8"
              keyboardType="number-pad"
              value={fechaVto}
              onChangeText={handleFechaVtoChange}
              maxLength={5}
            />
          </View>

          <View style={styles.inlineField}>
            <Text style={styles.label}>CVV</Text>
            <TextInput
              style={styles.input}
              placeholder="CVV"
              placeholderTextColor="#94A3B8"
              keyboardType="number-pad"
              secureTextEntry
              value={cvv}
              onChangeText={handleCvvChange}
              maxLength={4}
            />
          </View>
        </View>

        <Pressable
          style={[styles.primaryButton, loading && styles.disabledButton]}
          onPress={guardarTarjeta}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.primaryText}>Guardar tarjeta</Text>
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
    marginTop: 10,
    marginBottom: 8,
    fontSize: 28,
    color: "#0F172A",
    fontWeight: "900",
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
    color: "#64748B",
    fontSize: 12,
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
  inlineRow: { flexDirection: "row", gap: 12 },
  inlineField: { flex: 1 },
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
