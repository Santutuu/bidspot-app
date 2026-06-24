import { getRegistrationStatus } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import { router, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function RegistrationStatusScreen() {
  const params = useLocalSearchParams<{ mail?: string }>();
  const { pendingRegistrationMail, setPendingMail } = useAuth();

  const initialMail = params.mail ?? pendingRegistrationMail ?? "";
  const [mail, setMail] = useState(initialMail);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialMail) {
      handleCheckStatus(initialMail, false);
    }
  }, []);

  async function handleCheckStatus(mailParam?: string, showPendingAlert = true) {
    const mailToCheck = (mailParam ?? mail).trim().toLowerCase();

    if (!mailToCheck) {
      Alert.alert("Email requerido", "Volvé al registro o ingresá con tu cuenta.");
      return;
    }

    try {
      setLoading(true);
      await setPendingMail(mailToCheck);

      const response = await getRegistrationStatus(mailToCheck);

      if (response.estado === "VALIDADO" && response.puedeGenerarClave) {
        router.replace({
          pathname: "/(tabs)/auth/complete-registration" as any,
          params: { mail: response.mail },
        });
        return;
      }

      if (response.estado === "RECHAZADO") {
        Alert.alert("Solicitud rechazada", "La empresa rechazó tu solicitud.");
        return;
      }

      if (response.estado === "BLOQUEADO") {
        Alert.alert("Cuenta bloqueada", "Tu cuenta se encuentra bloqueada.");
        return;
      }

      if (showPendingAlert) {
        Alert.alert("Cuenta en revisión", response.mensaje);
      }
    } catch (error: any) {
      Alert.alert(
        "Error",
        error.response?.data?.message ?? "No pudimos consultar tu estado."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Tu cuenta se encuentra en revisión</Text>

      <Text style={styles.subtitle}>
        En breve nos pondremos en contacto. Cuando la empresa valide tu cuenta,
        vas a poder generar tu clave personal.
      </Text>

      <Text style={styles.mail}>{mail}</Text>

      <Text style={styles.hourglass}>⌛</Text>

      <Pressable
        style={[styles.button, loading && styles.disabled]}
        onPress={() => handleCheckStatus()}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="#27447F" />
        ) : (
          <Text style={styles.buttonText}>Actualizar estado</Text>
        )}
      </Pressable>

      <Pressable
        style={styles.linkButton}
        onPress={() => router.replace("/(tabs)/home")}
      >
        <Text style={styles.linkText}>Volver al inicio</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#27447F",
    paddingHorizontal: 28,
    paddingTop: 70,
    paddingBottom: 34,
  },

  title: {
    color: "white",
    fontSize: 28,
    fontWeight: "800",
    lineHeight: 37,
    marginBottom: 28,
  },

  subtitle: {
    color: "white",
    fontSize: 24,
    lineHeight: 34,
    marginBottom: 26,
  },

  mail: {
    color: "#DBEAFE",
    fontSize: 15,
    fontWeight: "800",
    marginBottom: 38,
  },

  hourglass: {
    color: "white",
    fontSize: 82,
    textAlign: "center",
    marginBottom: 44,
  },

  button: {
    backgroundColor: "white",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
  },

  buttonText: {
    color: "#27447F",
    fontSize: 16,
    fontWeight: "900",
  },

  linkButton: {
    paddingVertical: 15,
    alignItems: "center",
    marginTop: 12,
  },

  linkText: {
    color: "white",
    fontSize: 15,
    fontWeight: "800",
  },

  disabled: {
    opacity: 0.75,
  },
});