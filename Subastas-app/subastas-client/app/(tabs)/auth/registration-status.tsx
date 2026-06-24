import { getRegistrationStatus } from "@/src/api/authAPI";
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

export default function RegistrationStatusScreen() {
  const [mail, setMail] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleCheckStatus() {
    if (!mail.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu email.");
      return;
    }

    try {
      setLoading(true);

      const response = await getRegistrationStatus(
        mail.trim().toLowerCase()
      );

      if (response.estado === "PENDIENTE_VALIDACION") {
        Alert.alert(
          "Cuenta en revisión",
          "Tu solicitud todavía está siendo evaluada por la empresa."
        );
        return;
      }

      if (response.estado === "RECHAZADO") {
        Alert.alert(
          "Solicitud rechazada",
          "Tu solicitud fue rechazada por la empresa."
        );
        return;
      }

      if (response.estado === "BLOQUEADO") {
        Alert.alert(
          "Cuenta bloqueada",
          "Tu cuenta se encuentra bloqueada."
        );
        return;
      }

      if (response.estado === "VALIDADO") {
        Alert.alert(
          "Cuenta validada",
          "Tu cuenta fue aprobada. Ahora debés generar tu contraseña."
        );

        router.replace({
          pathname: "/auth/complete-registration",
          params: {
            mail: response.mail,
          },
        });

        return;
      }
    } catch (error: any) {
      const message =
        error.response?.data?.message ??
        "No pudimos verificar el estado de tu solicitud.";

      Alert.alert("Error", message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.container}
    >
      <Text style={styles.kicker}>Estado de registro</Text>

      <Text style={styles.title}>Consultar solicitud</Text>

      <Text style={styles.subtitle}>
        Ingresá el mail utilizado durante el registro para verificar el estado
        de tu cuenta.
      </Text>

      <View style={styles.card}>
        <Text style={styles.emoji}>📩</Text>

        <Text style={styles.cardTitle}>Estado de validación</Text>

        <Text style={styles.cardText}>
          Cuando la empresa finalice la revisión de tus datos, podrás continuar
          con el proceso de registro.
        </Text>

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

        <Pressable
          style={[styles.primaryButton, loading && styles.buttonDisabled]}
          onPress={handleCheckStatus}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <Text style={styles.primaryButtonText}>
              Consultar estado
            </Text>
          )}
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F5F6FA",
  },

  container: {
    paddingHorizontal: 24,
    paddingTop: 42,
    paddingBottom: 40,
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
    marginBottom: 28,
  },

  card: {
    backgroundColor: "white",
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