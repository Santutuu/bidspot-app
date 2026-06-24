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
      <Pressable
        style={styles.backButton}
        onPress={() =>
          router.replace("/(tabs)/financial-setup/medios-pago" as any)
        }
      >
        <Text style={styles.backText}>‹</Text>
      </Pressable>

      <Text style={styles.title}>Tarjeta Principal</Text>

      <TextInput
        style={styles.input}
        placeholder="Nro tarjeta"
        placeholderTextColor="#9CA3AF"
        keyboardType="numeric"
        value={numero}
        onChangeText={setNumero}
      />

      <TextInput
        style={styles.input}
        placeholder="Nombre"
        placeholderTextColor="#9CA3AF"
        value={nombre}
        onChangeText={setNombre}
      />

      <TextInput
        style={styles.smallInput}
        placeholder="F vto"
        placeholderTextColor="#9CA3AF"
        value={fechaVto}
        onChangeText={setFechaVto}
      />

      <TextInput
        style={styles.smallInput}
        placeholder="CVV"
        placeholderTextColor="#9CA3AF"
        keyboardType="numeric"
        secureTextEntry
        value={cvv}
        onChangeText={setCvv}
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
          onPress={guardarTarjeta}
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
    marginBottom: 28,
  },

  input: {
    height: 48,
    borderWidth: 1.3,
    borderColor: "#111827",
    borderRadius: 8,
    paddingHorizontal: 12,
    marginBottom: 12,
    color: "#111827",
  },

  smallInput: {
    width: 86,
    height: 42,
    borderWidth: 1.3,
    borderColor: "#111827",
    borderRadius: 8,
    paddingHorizontal: 10,
    marginBottom: 10,
    color: "#111827",
  },

  actions: {
    marginTop: 42,
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
