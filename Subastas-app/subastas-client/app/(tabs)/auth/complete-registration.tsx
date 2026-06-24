import { completeRegistration } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import { router, useLocalSearchParams } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

export default function CompleteRegistrationScreen() {
  const params = useLocalSearchParams<{ mail?: string }>();
  const { login, pendingRegistrationMail } = useAuth();

  const [mail, setMail] = useState(params.mail ?? pendingRegistrationMail ?? "");
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

      router.replace("/(tabs)/financial-setup" as any);
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ??
          error.response?.data?.error ??
          "No pudimos completar el registro."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.keyboardContainer}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
        <Text style={styles.title}>Generá tu clave</Text>

        <View style={styles.messageBox}>
          <Text style={styles.messageTitle}>Cuenta validada exitosamente.</Text>
          <Text style={styles.messageText}>Tu categoría es: PLATA.</Text>
          <Text style={styles.messageText}>
            Generá clave e ingresá medios de pago para completar registro.
          </Text>
        </View>

        <Text style={styles.mail}>{mail}</Text>

        <TextInput
          style={styles.input}
          placeholder="Ingresá tu contraseña"
          placeholderTextColor="#6B7280"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        <TextInput
          style={styles.input}
          placeholder="Confirmá contraseña"
          placeholderTextColor="#6B7280"
          secureTextEntry
          value={confirmPassword}
          onChangeText={setConfirmPassword}
        />

        <View style={styles.actionRow}>
          <Pressable
            style={styles.cancelButton}
            onPress={() => router.replace("/(tabs)/home")}
            disabled={loading}
          >
            <Text style={styles.cancelIcon}>×</Text>
          </Pressable>

          <Pressable
            style={[styles.confirmButton, loading && styles.disabled]}
            onPress={handleCompleteRegistration}
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
    </KeyboardAvoidingView>
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
  keyboardContainer: {
    flex: 1,
  },

  screen: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },

  container: {
    flexGrow: 1,
    paddingHorizontal: 26,
    paddingTop: 48,
    paddingBottom: 42,
  },

  title: {
    fontSize: 31,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 26,
  },

  messageBox: {
    backgroundColor: "#EFF6FF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#BFDBFE",
    padding: 16,
    marginBottom: 28,
  },

  messageTitle: {
    fontSize: 15,
    color: "#111827",
    fontWeight: "900",
    marginBottom: 6,
  },

  messageText: {
    fontSize: 14,
    color: "#374151",
    lineHeight: 20,
  },

  mail: {
    color: "#111827",
    fontSize: 16,
    fontWeight: "800",
    marginBottom: 24,
  },

  input: {
    height: 56,
    borderWidth: 1.4,
    borderColor: "#111827",
    borderRadius: 10,
    paddingHorizontal: 14,
    fontSize: 15,
    color: "#111827",
    marginBottom: 14,
    backgroundColor: "#FFFFFF",
  },

  actionRow: {
    marginTop: 32,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
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
    backgroundColor: "#FFFFFF",
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

  disabled: {
    opacity: 0.7,
  },
});