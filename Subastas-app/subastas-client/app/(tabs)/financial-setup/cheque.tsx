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
      <View style={styles.headerBlock}>
        <Text style={styles.kicker}>Medio de cobro</Text>
        <Text style={styles.title}>Cheque certificado</Text>
        <Text style={styles.subtitle}>
          Cargá los datos del cheque con el mismo criterio que usa la empresa
          para validar medios de pago.
        </Text>
      </View>

      <View style={styles.formCard}>
        <Text style={styles.label}>Identificación</Text>
        <TextInput
          style={styles.input}
          keyboardType="numeric"
          value={identificacion}
          onChangeText={setIdentificacion}
        />

        <Text style={styles.label}>Número de cheque</Text>
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

        <Text style={styles.label}>CUIL / CUIT</Text>
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

        <View style={styles.noteBox}>
          <Text style={styles.noteTitle}>Control interno</Text>
          <Text style={styles.noteText}>
            Verificá que el beneficiario, la identificación y el saldo coincidan
            con la documentación respaldatoria.
          </Text>
        </View>

        <View style={styles.actions}>
          <Pressable
            style={styles.secondaryButton}
            onPress={() =>
              router.replace("/(tabs)/financial-setup/medios-pago" as any)
            }
          >
            <Text style={styles.secondaryButtonText}>Volver</Text>
          </Pressable>

          <Pressable
            style={styles.primaryButton}
            onPress={guardarCheque}
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

  headerBlock: {
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
    shadowOpacity: 0.03,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 1,
  },

  label: {
    color: "#64748B",
    fontSize: 12,
    fontWeight: "900",
    marginBottom: 8,
    textTransform: "uppercase",
    letterSpacing: 0.8,
  },

  input: {
    height: 52,
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
