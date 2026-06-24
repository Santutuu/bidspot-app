import { crearCheque } from "@/src/api/meAPI";
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

export default function ChequeScreen() {
  const [identificacion, setIdentificacion] = useState("");
  const [nroCheque, setNroCheque] = useState("");
  const [beneficiario, setBeneficiario] = useState("");
  const [cuilCuit, setCuilCuit] = useState("");
  const [saldo, setSaldo] = useState("");
  const [loading, setLoading] = useState(false);

  async function guardarCheque() {
    if (
      !identificacion.trim() ||
      !nroCheque.trim() ||
      !beneficiario.trim() ||
      !cuilCuit.trim() ||
      !saldo.trim()
    ) {
      Alert.alert("Campos obligatorios", "Completá todos los campos.");
      return;
    }

    try {
      setLoading(true);

      await crearCheque({
        identificacion: Number(identificacion),
        nroCheque: nroCheque.trim(),
        beneficiario: beneficiario.trim(),
        cuilCuit: cuilCuit.trim(),
        saldo: Number(saldo),
      });

      Alert.alert("Cheque registrado", "Tu cheque fue guardado.");
      router.replace({ pathname: "/financial-setup"});

      
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ?? "No pudimos guardar el cheque."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Medio de pago</Text>
      <Text style={styles.title}>Cheque certificado</Text>

      <View style={styles.card}>
        <TextInput
          style={styles.input}
          placeholder="Identificación"
          placeholderTextColor="#9CA3AF"
          keyboardType="numeric"
          value={identificacion}
          onChangeText={setIdentificacion}
        />

        <TextInput
          style={styles.input}
          placeholder="Número cheque"
          placeholderTextColor="#9CA3AF"
          value={nroCheque}
          onChangeText={setNroCheque}
        />

        <TextInput
          style={styles.input}
          placeholder="Beneficiario"
          placeholderTextColor="#9CA3AF"
          value={beneficiario}
          onChangeText={setBeneficiario}
        />

        <TextInput
          style={styles.input}
          placeholder="CUIL/CUIT"
          placeholderTextColor="#9CA3AF"
          value={cuilCuit}
          onChangeText={setCuilCuit}
        />

        <TextInput
          style={styles.input}
          placeholder="Saldo"
          placeholderTextColor="#9CA3AF"
          keyboardType="numeric"
          value={saldo}
          onChangeText={setSaldo}
        />

        <View style={styles.actionsRow}>
          <Pressable style={styles.secondaryButton} onPress={() => router.back()}>
            <Text style={styles.secondaryButtonText}>Cancelar</Text>
          </Pressable>

          <Pressable
            style={[styles.primaryButton, loading && styles.disabled]}
            onPress={guardarCheque}
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
  screen: { flex: 1, backgroundColor: "#F5F6FA" },
  container: { paddingHorizontal: 22, paddingTop: 34, paddingBottom: 42 },

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

  actionsRow: { flexDirection: "row", gap: 12, marginTop: 8 },

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

  disabled: { opacity: 0.7 },
});