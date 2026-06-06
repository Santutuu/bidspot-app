import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

export default function RegisterScreen() {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Crear cuenta</Text>
      <Text style={styles.subtitle}>
        Completá tus datos para iniciar el registro como postor.
      </Text>

      <TextInput style={styles.input} placeholder="Nombre" placeholderTextColor="#888" />
      <TextInput style={styles.input} placeholder="Apellido" placeholderTextColor="#888" />
      <TextInput style={styles.input} placeholder="Email" placeholderTextColor="#888" />
      <TextInput style={styles.input} placeholder="Domicilio legal" placeholderTextColor="#888" />

      <Pressable style={styles.uploadBox}>
        <Text style={styles.uploadTitle}>Foto documento</Text>
        <Text style={styles.uploadSubtitle}>Frente y dorso del DNI</Text>
      </Pressable>

      <Pressable style={styles.button}>
        <Text style={styles.buttonText}>Enviar solicitud</Text>
      </Pressable>

      <Pressable onPress={() => router.push("/auth/login")}>
        <Text style={styles.link}>Ya tengo cuenta</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F7F8FC",
  },

  container: {
    paddingHorizontal: 24,
    paddingTop: 70,
    paddingBottom: 40,
  },

  title: {
    fontSize: 32,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 8,
  },

  subtitle: {
    fontSize: 16,
    color: "#6B7280",
    lineHeight: 23,
    marginBottom: 28,
  },

  input: {
    backgroundColor: "white",
    borderRadius: 14,
    paddingHorizontal: 16,
    paddingVertical: 15,
    fontSize: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: "#E5E7EB",
  },

  uploadBox: {
    backgroundColor: "white",
    borderRadius: 16,
    padding: 18,
    borderWidth: 1,
    borderColor: "#D1D5DB",
    borderStyle: "dashed",
    marginBottom: 20,
  },

  uploadTitle: {
    fontSize: 16,
    fontWeight: "700",
    color: "#111827",
  },

  uploadSubtitle: {
    marginTop: 4,
    fontSize: 14,
    color: "#6B7280",
  },

  button: {
    backgroundColor: "#2F63F6",
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: "center",
  },

  buttonText: {
    color: "white",
    fontSize: 17,
    fontWeight: "700",
  },

  link: {
    textAlign: "center",
    color: "#2F63F6",
    fontSize: 15,
    fontWeight: "600",
    marginTop: 22,
  },
});