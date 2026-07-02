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

  const mail = params.mail ?? pendingRegistrationMail ?? "";
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
          "No pudimos completar el registro.",
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
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.heroCard}>
          <Text style={styles.kicker}>Registro validado</Text>
          <Text style={styles.title}>Creá tu acceso personal</Text>
          <Text style={styles.subtitle}>
            Definí una contraseña segura para terminar de activar tu cuenta y
            seguir con los medios de pago.
          </Text>
          <Text style={styles.mail}>{mail}</Text>
        </View>

        <View style={styles.formCard}>
          <View style={styles.messageBox}>
            <Text style={styles.messageTitle}>Tu cuenta fue aprobada</Text>
            <Text style={styles.messageText}>
              La contraseña queda asociada a tu usuario y vas a poder continuar
              con la configuración financiera.
            </Text>
          </View>

          <Text style={styles.inputLabel}>Contraseña</Text>
          <TextInput
            style={styles.input}
            placeholder="Ingresá tu contraseña"
            placeholderTextColor="#94A3B8"
            secureTextEntry
            value={password}
            onChangeText={setPassword}
          />

          <Text style={styles.inputLabel}>Confirmación</Text>
          <TextInput
            style={styles.input}
            placeholder="Repetí tu contraseña"
            placeholderTextColor="#94A3B8"
            secureTextEntry
            value={confirmPassword}
            onChangeText={setConfirmPassword}
          />

          <View style={styles.helperRow}>
            <View style={styles.helperDot} />
            <Text style={styles.helperText}>
              Usá al menos 8 caracteres, una mayúscula, un número y un símbolo.
            </Text>
          </View>

          <View style={styles.actionRow}>
            <Pressable
              style={styles.cancelButton}
              onPress={() => router.replace("/(tabs)/home")}
              disabled={loading}
            >
              <Text style={styles.cancelButtonText}>Volver</Text>
            </Pressable>

            <Pressable
              style={[styles.confirmButton, loading && styles.disabled]}
              onPress={handleCompleteRegistration}
              disabled={loading}
            >
              {loading ? (
                <ActivityIndicator color="#FFFFFF" />
              ) : (
                <Text style={styles.confirmButtonText}>Confirmar</Text>
              )}
            </Pressable>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

function getPasswordError(value: string) {
  if (value.length < 8)
    return "La contraseña debe tener al menos 8 caracteres.";
  if (!/[A-Z]/.test(value))
    return "La contraseña debe tener al menos una mayúscula.";
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
    backgroundColor: "#F2F5FB",
  },

  container: {
    flexGrow: 1,
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
    fontWeight: "900",
    color: "#0F172A",
    lineHeight: 34,
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#475569",
    lineHeight: 22,
    marginBottom: 8,
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

  messageBox: {
    backgroundColor: "#F8FAFC",
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 16,
    marginBottom: 18,
  },

  messageTitle: {
    fontSize: 15,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 6,
  },

  messageText: {
    fontSize: 14,
    color: "#475569",
    lineHeight: 20,
  },

  mail: {
    color: "#64748B",
    fontSize: 15,
    fontWeight: "800",
    marginTop: 4,
  },

  inputLabel: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginTop: 6,
    marginBottom: 8,
  },

  input: {
    height: 56,
    borderWidth: 1.4,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    paddingHorizontal: 14,
    fontSize: 15,
    color: "#0F172A",
    marginBottom: 6,
    backgroundColor: "#F8FAFC",
  },

  helperRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 8,
    marginTop: 10,
  },

  helperDot: {
    width: 8,
    height: 8,
    borderRadius: 999,
    backgroundColor: "#2F63F6",
    marginTop: 6,
  },

  helperText: {
    flex: 1,
    fontSize: 13,
    lineHeight: 19,
    color: "#64748B",
    fontWeight: "600",
  },

  actionRow: {
    marginTop: 22,
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },

  cancelButton: {
    flex: 1,
    height: 54,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
  },

  confirmButton: {
    flex: 1.2,
    height: 54,
    borderRadius: 12,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
  },

  cancelButtonText: {
    fontSize: 15,
    color: "#111827",
    fontWeight: "900",
  },

  confirmButtonText: {
    fontSize: 15,
    color: "#FFFFFF",
    fontWeight: "900",
  },

  disabled: {
    opacity: 0.7,
  },
});
