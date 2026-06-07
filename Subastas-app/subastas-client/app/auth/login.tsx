import { loginUser } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import { router } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

export default function LoginScreen() {
  const { login } = useAuth();

  const [mail, setMail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin() {
    if (!mail.trim()) {
      return Alert.alert("Campo obligatorio", "Ingresá tu email.");
    }

    if (!password.trim()) {
      return Alert.alert("Campo obligatorio", "Ingresá tu contraseña.");
    }

    try {
      setLoading(true);

      const response = await loginUser({
        mail: mail.trim(),
        password,
      });

      await login(response);

      router.replace("/(tabs)/home");
    } catch (error) {
      console.error("Error login:", error);

      Alert.alert(
        "No pudimos iniciar sesión",
        "Revisá tu email y contraseña e intentá nuevamente."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.kicker}>Acceso de usuario</Text>

      <Text style={styles.title}>Iniciar sesión</Text>

      <Text style={styles.subtitle}>
        Ingresá para guardar subastas, ver detalles y participar cuando tu cuenta esté validada.
      </Text>

      <TextInput
        style={styles.input}
        placeholder="Email"
        placeholderTextColor="#9CA3AF"
        keyboardType="email-address"
        autoCapitalize="none"
        value={mail}
        onChangeText={setMail}
      />

      <TextInput
        style={styles.input}
        placeholder="Contraseña"
        placeholderTextColor="#9CA3AF"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      <Pressable
        style={[styles.button, loading && styles.buttonDisabled]}
        onPress={handleLogin}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="white" />
        ) : (
          <Text style={styles.buttonText}>Ingresar</Text>
        )}
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
    backgroundColor: "#F5F6FA",
    paddingHorizontal: 24,
    justifyContent: "center",
  },

  kicker: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "800",
    letterSpacing: 0.6,
    textTransform: "uppercase",
    marginBottom: 8,
  },

  title: {
    fontSize: 34,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 30,
  },

  input: {
    backgroundColor: "#FFFFFF",
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

  button: {
    backgroundColor: "#2F63F6",
    paddingVertical: 16,
    borderRadius: 13,
    alignItems: "center",
    marginTop: 8,
  },

  buttonDisabled: {
    opacity: 0.7,
  },

  buttonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },

  link: {
    textAlign: "center",
    color: "#2F63F6",
    fontSize: 15,
    fontWeight: "700",
    marginTop: 22,
  },
});