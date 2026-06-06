import { router } from "expo-router";
import { Pressable, StyleSheet, Text, TextInput, View } from "react-native";

export default function LoginScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Iniciar sesión</Text>
      <Text style={styles.subtitle}>Ingresá para participar en subastas.</Text>

      <TextInput style={styles.input} placeholder="Usuario o email" placeholderTextColor="#888" />
      <TextInput
        style={styles.input}
        placeholder="Contraseña"
        placeholderTextColor="#888"
        secureTextEntry
      />

      <Pressable style={styles.button}>
        <Text style={styles.buttonText}>Ingresar</Text>
      </Pressable>

      <Pressable onPress={() => router.push("/auth/register")}>
        <Text style={styles.link}>¿No tenés cuenta? Registrate</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F7F8FC",
    paddingHorizontal: 24,
    justifyContent: "center",
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
    marginBottom: 30,
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

  button: {
    backgroundColor: "#2F63F6",
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: "center",
    marginTop: 8,
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