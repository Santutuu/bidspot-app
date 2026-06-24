import { completeRegistration } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import { router, useLocalSearchParams } from "expo-router";
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

export default function CompleteRegistrationScreen() {
  const params = useLocalSearchParams<{ mail?: string }>();
  const { login } = useAuth();

  const [mail, setMail] = useState(params.mail ?? "");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleCompleteRegistration() {
    if (!mail.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu email.");
      return;
    }

    const passwordError = getPasswordError(password);

    if (passwordError) {
      Alert.alert("Contraseña inválida", passwordError);
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert("Las contraseñas no coinciden", "Revisá la confirmación.");
      return;
    }

    try {
      setLoading(true);

      const response = await completeRegistration({
        mail: mail.trim().toLowerCase(),
        password,
        confirmPassword,
      });

      await login(response);

      Alert.alert(
        "Registro completado",
        "Tu clave fue generada correctamente. Ahora completá tu configuración financiera."
      );

      router.replace("/financial-setup");
    } catch (error: any) {
      const message =
        error.response?.data?.message ??
        error.response?.data?.error ??
        "No pudimos completar el registro.";

      Alert.alert("Error", message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Cuenta validada</Text>

      <Text style={styles.title}>Generá tu clave</Text>

      <Text style={styles.subtitle}>
        Tu cuenta ya fue aceptada por la empresa. Ahora creá tu clave personal
        para ingresar y finalizar el registro.
      </Text>

      <View style={styles.card}>
        <Text style={styles.emoji}>🔐</Text>

        <Text style={styles.cardTitle}>Clave personal</Text>

        <Text style={styles.cardText}>
          El email corresponde a tu solicitud validada. Completá tu clave y
          confirmala para continuar con cuenta de cobro y medios de pago.
        </Text>

        <Text style={styles.label}>Mail</Text>
        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="#9CA3AF"
          keyboardType="email-address"
          autoCapitalize="none"
          autoCorrect={false}
          value={mail}
          onChangeText={setMail}
        />

        <Text style={styles.label}>Contraseña</Text>
        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          placeholderTextColor="#9CA3AF"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        <Text style={styles.label}>Repetir contraseña</Text>
        <TextInput
          style={styles.input}
          placeholder="Repetir contraseña"
          placeholderTextColor="#9CA3AF"
          secureTextEntry
          value={confirmPassword}
          onChangeText={setConfirmPassword}
        />

        <Text style={styles.hint}>
          Mínimo 8 caracteres, una mayúscula, un número y un carácter especial.
        </Text>

        <Pressable
          style={[styles.primaryButton, loading && styles.buttonDisabled]}
          onPress={handleCompleteRegistration}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <Text style={styles.primaryButtonText}>Guardar clave</Text>
          )}
        </Pressable>
      </View>
    </ScrollView>
  );
}

function getPasswordError(value: string) {
  if (value.length < 8) return "La contraseña debe tener al menos 8 caracteres.";
  if (!/[A-Z]/.test(value)) return "La contraseña debe tener al menos una mayúscula.";
  if (!/\d/.test(value)) return "La contraseña debe tener al menos un número.";
  if (!/[^A-Za-z0-9]/.test(value)) {
    return "La contraseña debe tener al menos un carácter especial.";
  }

  return null;
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F5F6FA",
  },

  container: {
    paddingHorizontal: 22,
    paddingTop: 34,
    paddingBottom: 42,
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
    fontSize: 32,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 26,
  },

  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 24,
    padding: 22,
    borderWidth: 1,
    borderColor: "#E5E7EB",
  },

  emoji: {
    fontSize: 38,
    marginBottom: 12,
  },

  cardTitle: {
    fontSize: 23,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 10,
  },

  cardText: {
    fontSize: 15,
    color: "#4B5563",
    lineHeight: 23,
    marginBottom: 20,
  },

  label: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 6,
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

  hint: {
    fontSize: 12,
    color: "#6B7280",
    lineHeight: 18,
    marginBottom: 18,
  },

  primaryButton: {
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
  },

  primaryButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },

  buttonDisabled: {
    opacity: 0.7,
  },
});