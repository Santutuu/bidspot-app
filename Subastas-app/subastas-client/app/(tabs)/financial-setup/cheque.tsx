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

      const response = await crearCheque({
        identificacion: Number(identificacion),
        nroCheque: nroCheque.trim(),
        beneficiario: beneficiario.trim(),
        cuilCuit: cuilCuit.trim(),
        saldo: Number(saldo),
      });

      const successMessage =
        (response as any)?.message ?? "El cheque se guardó correctamente.";

      Alert.alert("Éxito", successMessage, [
        {
          text: "OK",
          onPress: () =>
            router.replace("/(tabs)/financial-setup/medios-pago" as any),
        },
      ]);
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ??
          error.response?.data?.error ??
          "No pudimos guardar el cheque.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Pressable
        style={styles.backButton}
        onPress={() =>
          router.replace("/(tabs)/financial-setup/medios-pago" as any)
        }
      >
        <Text style={styles.backText}>‹</Text>
      </Pressable>

      <Text style={styles.title}>Cheque certificado</Text>

      <Text style={styles.label}>Identificación</Text>
      <TextInput
        style={styles.input}
        keyboardType="numeric"
        value={identificacion}
        onChangeText={setIdentificacion}
      />

      <Text style={styles.label}>Número cheque</Text>
      <TextInput
        style={styles.input}
        value={nroCheque}
        onChangeText={setNroCheque}
      />

      <Text style={styles.label}>Beneficiario</Text>
      <TextInput
        style={styles.input}
        value={beneficiario}
        onChangeText={setBeneficiario}
      />

      <Text style={styles.label}>CUIL/CUIT</Text>
      <TextInput
        style={styles.input}
        value={cuilCuit}
        onChangeText={setCuilCuit}
      />

      <Text style={styles.label}>Saldo</Text>
      <TextInput
        style={styles.input}
        keyboardType="numeric"
        value={saldo}
        onChangeText={setSaldo}
      />

      <View style={styles.actions}>
        <Pressable
          style={styles.cancelButton}
          onPress={() =>
            router.replace("/(tabs)/financial-setup/medios-pago" as any)
          }
        >
          <Text style={styles.cancelIcon}>×</Text>
        </Pressable>

        <Pressable
          style={styles.confirmButton}
          onPress={guardarCheque}
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
    fontSize: 22,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 24,
  },

  label: {
    color: "#111827",
    fontSize: 12,
    fontWeight: "900",
    marginBottom: 4,
  },

  input: {
    height: 43,
    borderWidth: 1.3,
    borderColor: "#111827",
    paddingHorizontal: 10,
    marginBottom: 10,
    color: "#111827",
  },

  actions: {
    marginTop: 34,
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
