import { crearTarjeta } from "@/src/api/meAPI";
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

export default function TarjetaScreen() {
  const [numero, setNumero] = useState("");
  const [nombre, setNombre] = useState("");
  const [fechaVto, setFechaVto] = useState("");
  const [cvv, setCvv] = useState("");
  const [loading, setLoading] = useState(false);

  async function guardarTarjeta() {
    if (!numero.trim() || !nombre.trim() || !fechaVto.trim() || !cvv.trim()) {
      Alert.alert("Campos obligatorios", "Completá todos los campos.");
      return;
    }

    try {
      setLoading(true);

      const response = await crearTarjeta({
        numero: numero.trim(),
        nombre: nombre.trim(),
        fechaVto: fechaVto.trim(),
        cvv: cvv.trim(),
      });

      const successMessage =
        (response as any)?.message ?? "La tarjeta se guardó correctamente.";

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
          "No pudimos guardar la tarjeta.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <View style={styles.heroCard}>
        <Text style={styles.kicker}>Medio de pago</Text>
        <Text style={styles.title}>Tarjeta principal</Text>
        <Text style={styles.subtitle}>
          Registrá la tarjeta que vas a usar como respaldo para operaciones y
          validaciones.
        </Text>
      </View>

      <View style={styles.formCard}>
        <Text style={styles.label}>Número de tarjeta</Text>
        <TextInput
          style={styles.input}
          placeholder="Nro tarjeta"
          placeholderTextColor="#94A3B8"
          keyboardType="numeric"
          value={numero}
          onChangeText={setNumero}
        />

        <Text style={styles.label}>Titular</Text>
        <TextInput
          style={styles.input}
          placeholder="Nombre del titular"
          placeholderTextColor="#94A3B8"
          value={nombre}
          onChangeText={setNombre}
        />

        <View style={styles.inlineRow}>
          <View style={styles.inlineField}>
            <Text style={styles.label}>Vencimiento</Text>
            <TextInput
              style={styles.input}
              placeholder="MM/AA"
              placeholderTextColor="#94A3B8"
              value={fechaVto}
              onChangeText={setFechaVto}
            />
          </View>

          <View style={styles.inlineField}>
            <Text style={styles.label}>CVV</Text>
            <TextInput
              style={styles.input}
              placeholder="CVV"
              placeholderTextColor="#94A3B8"
              keyboardType="numeric"
              secureTextEntry
              value={cvv}
              onChangeText={setCvv}
            />
          </View>
        </View>

        <View style={styles.noteBox}>
          <Text style={styles.noteTitle}>Seguridad</Text>
          <Text style={styles.noteText}>
            Revisá que los datos estén completos y coincidan con la tarjeta
            física.
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
            onPress={guardarTarjeta}
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
    color: "#64748B",
    fontSize: 12,
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 8,
  },

  input: {
    height: 52,
    borderWidth: 1.3,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    paddingHorizontal: 12,
    marginBottom: 14,
    color: "#0F172A",
    backgroundColor: "#F8FAFC",
  },

  inlineRow: {
    flexDirection: "row",
    gap: 12,
  },

  inlineField: {
    flex: 1,
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
