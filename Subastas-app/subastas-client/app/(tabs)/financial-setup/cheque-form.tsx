import { crearCheque } from "@/src/api/meAPI";
import { MonedaMedioPago } from "@/src/dto/me/MedioPagoDTO";
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
    "No pudimos guardar el cheque."
  );
}

export default function ChequeFormScreen() {
  const [identificacion, setIdentificacion] = useState("");
  const [nroCheque, setNroCheque] = useState("");
  const [beneficiario, setBeneficiario] = useState("");
  const [cuilCuit, setCuilCuit] = useState("");
  const [saldo, setSaldo] = useState("");
  const [moneda, setMoneda] = useState<MonedaMedioPago>("PESOS");
  const [loading, setLoading] = useState(false);

  function handleNroChequeChange(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 20);
    setNroCheque(digits);
  }

  function handleCuilCuitChange(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 11);
    setCuilCuit(digits);
  }

  function handleSaldoChange(value: string) {
    const normalized = value.replace(/,/g, ".").replace(/[^\d.]/g, "");
    const parts = normalized.split(".");

    if (parts.length > 2) {
      setSaldo(`${parts[0]}.${parts.slice(1).join("")}`);
      return;
    }

    if (parts.length === 2) {
      setSaldo(`${parts[0]}.${parts[1].slice(0, 2)}`);
      return;
    }

    setSaldo(parts[0]);
  }

  function validate() {
    if (identificacion.trim().length < 2)
      return "Identificacion es obligatoria.";
    if (!/^\d+$/.test(nroCheque.trim()))
      return "Numero de cheque es obligatorio.";
    if (beneficiario.trim().length < 3) return "Beneficiario es obligatorio.";
    if (!/^\d{11}$/.test(cuilCuit.trim())) {
      return "CUIL/CUIT debe tener 11 digitos numericos.";
    }

    const saldoNumber = Number(saldo);
    if (Number.isNaN(saldoNumber) || saldoNumber <= 0) {
      return "Saldo debe ser un numero mayor a cero.";
    }

    return null;
  }

  async function guardarCheque() {
    const validationError = validate();

    if (validationError) {
      Alert.alert("Revisa los datos", validationError);
      return;
    }

    try {
      setLoading(true);

      await crearCheque({
        identificacion: identificacion.trim(),
        nroCheque: nroCheque.trim(),
        beneficiario: beneficiario.trim(),
        cuilCuit: cuilCuit.trim(),
        saldo: Number(saldo),
        moneda,
      });

      Alert.alert("Cheque guardado", "El cheque se agrego correctamente.", [
        {
          text: "OK",
          onPress: () =>
            router.replace("/(tabs)/financial-setup/cheque" as any),
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

      <Text style={styles.title}>Agregar cheque</Text>
      <Text style={styles.subtitle}>
        Carga los datos para validar el cheque.
      </Text>

      <View style={styles.formCard}>
        <Text style={styles.label}>Identificacion</Text>
        <TextInput
          style={styles.input}
          value={identificacion}
          onChangeText={setIdentificacion}
          placeholder="Identificacion interna"
          placeholderTextColor="#94A3B8"
        />

        <Text style={styles.label}>Numero cheque</Text>
        <TextInput
          style={styles.input}
          keyboardType="number-pad"
          value={nroCheque}
          onChangeText={handleNroChequeChange}
          placeholder="Solo numeros"
          placeholderTextColor="#94A3B8"
        />

        <Text style={styles.label}>Beneficiario</Text>
        <TextInput
          style={styles.input}
          value={beneficiario}
          onChangeText={setBeneficiario}
          placeholder="Nombre del beneficiario"
          placeholderTextColor="#94A3B8"
        />

        <Text style={styles.label}>CUIL/CUIT</Text>
        <TextInput
          style={styles.input}
          keyboardType="number-pad"
          value={cuilCuit}
          onChangeText={handleCuilCuitChange}
          maxLength={11}
          placeholder="11 digitos"
          placeholderTextColor="#94A3B8"
        />

        <Text style={styles.label}>Saldo</Text>
        <TextInput
          style={styles.input}
          keyboardType="decimal-pad"
          value={saldo}
          onChangeText={handleSaldoChange}
          placeholder="Monto"
          placeholderTextColor="#94A3B8"
        />

        <Text style={styles.label}>Moneda</Text>
        <View style={styles.segmented}>
          <Pressable
            style={[
              styles.segmentButton,
              moneda === "PESOS" && styles.segmentButtonActive,
            ]}
            onPress={() => setMoneda("PESOS")}
          >
            <Text
              style={[
                styles.segmentText,
                moneda === "PESOS" && styles.segmentTextActive,
              ]}
            >
              ARS
            </Text>
          </Pressable>
          <Pressable
            style={[
              styles.segmentButton,
              moneda === "DOLARES" && styles.segmentButtonActive,
            ]}
            onPress={() => setMoneda("DOLARES")}
          >
            <Text
              style={[
                styles.segmentText,
                moneda === "DOLARES" && styles.segmentTextActive,
              ]}
            >
              USD
            </Text>
          </Pressable>
        </View>

        <Pressable
          style={[styles.primaryButton, loading && styles.disabledButton]}
          onPress={guardarCheque}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.primaryText}>Guardar cheque</Text>
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
  segmented: {
    flexDirection: "row",
    gap: 8,
    marginBottom: 16,
  },
  segmentButton: {
    flex: 1,
    height: 44,
    borderRadius: 12,
    borderWidth: 1.3,
    borderColor: "#CBD5E1",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#F8FAFC",
  },
  segmentButtonActive: {
    backgroundColor: "#111827",
    borderColor: "#111827",
  },
  segmentText: {
    color: "#475569",
    fontSize: 14,
    fontWeight: "900",
  },
  segmentTextActive: {
    color: "#FFFFFF",
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
