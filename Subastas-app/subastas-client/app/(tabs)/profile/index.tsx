import { getCurrentUser, getRegistrationStatus } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import { router, useFocusEffect } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

export default function ProfileScreen() {
  const {
    user,
    loadingAuth,
    isAuthenticated,
    isValidated,
    requiresPaymentSetup,
    pendingRegistrationMail,
    logout,
    refreshUser,
  } = useAuth();

  const [checkingStatus, setCheckingStatus] = useState(false);
  const refreshUserRef = useRef(refreshUser);

  useEffect(() => {
    refreshUserRef.current = refreshUser;
  }, [refreshUser]);

  useFocusEffect(
    useCallback(() => {
      if (loadingAuth) return;

      let active = true;

      async function evaluateProfileFlow() {
        try {
          setCheckingStatus(true);

          if (isAuthenticated) {
            const freshUser = await getCurrentUser();
            await refreshUserRef.current();

            if (!active) return;

            if (
              freshUser.estado === "VALIDADO" &&
              freshUser.claveGenerada &&
              freshUser.requiereMedioDePago
            ) {
              router.replace("/(tabs)/financial-setup" as any);
              return;
            }

            if (freshUser.estado === "VALIDADO" && !freshUser.claveGenerada) {
              router.replace({
                pathname: "/(tabs)/auth/complete-registration" as any,
                params: { mail: freshUser.mail },
              });
              return;
            }

            return;
          }

          if (!pendingRegistrationMail) return;

          const response = await getRegistrationStatus(pendingRegistrationMail);

          if (!active) return;

          if (response.estado === "VALIDADO" && response.puedeGenerarClave) {
            router.replace({
              pathname: "/(tabs)/auth/complete-registration" as any,
              params: { mail: response.mail },
            });
          }
        } catch {
          // En error de red mantenemos la UI actual para no romper la navegación.
        } finally {
          if (active) {
            setCheckingStatus(false);
          }
        }
      }

      void evaluateProfileFlow();

      return () => {
        active = false;
      };
    }, [loadingAuth, isAuthenticated, pendingRegistrationMail]),
  );

  async function handleLogout() {
    await logout();
    router.replace("/(tabs)/profile");
  }

  if (loadingAuth || checkingStatus) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#FFFFFF" />
        <Text style={styles.pendingText}>Cargando...</Text>
      </View>
    );
  }

  if (!isAuthenticated && pendingRegistrationMail) {
    return (
      <View style={styles.pendingContainer}>
        <Text style={styles.pendingTitle}>
          Tu cuenta se encuentra en revisión
        </Text>

        <Text style={styles.pendingSubtitle}>
          En breve nos pondremos en contacto. Cuando la empresa valide tu
          cuenta, vas a poder generar tu clave personal.
        </Text>

        <Text style={styles.mailText}>{pendingRegistrationMail}</Text>

        <Text style={styles.hourglass}>⌛</Text>

        <Pressable
          style={styles.transparentButton}
          onPress={() => router.replace("/(tabs)/home")}
        >
          <Text style={styles.transparentButtonText}>Volver al inicio</Text>
        </Pressable>
      </View>
    );
  }

  if (!isAuthenticated || !user) {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Tu perfil</Text>

        <Text style={styles.subtitle}>
          Iniciá sesión o registrate para participar en subastas.
        </Text>

        <Pressable
          style={styles.primaryButton}
          onPress={() => router.push("/auth/login")}
        >
          <Text style={styles.primaryButtonText}>Iniciar sesión</Text>
        </Pressable>

        <Pressable
          style={styles.secondaryButton}
          onPress={() => router.push("/auth/register")}
        >
          <Text style={styles.secondaryButtonText}>Crear cuenta</Text>
        </Pressable>
      </View>
    );
  }

  if (isValidated && requiresPaymentSetup) {
    return (
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.container}
      >
        <Text style={styles.title}>Hola {user.nombre}</Text>

        <Text style={styles.subtitle}>
          Agregá configuración financiera para completar tu registro.
        </Text>

        <View style={styles.card}>
          <Text style={styles.cardLabel}>Categoría</Text>
          <Text style={styles.category}>{user.categoria ?? "PLATA"}</Text>
        </View>

        <Pressable
          style={styles.optionCard}
          onPress={() =>
            router.push("/(tabs)/financial-setup/cuenta-cobro" as any)
          }
        >
          <Text style={styles.optionTitle}>Cuenta de cobro</Text>
          <Text style={styles.arrow}>→</Text>
        </Pressable>

        <Pressable
          style={styles.optionCard}
          onPress={() =>
            router.push("/(tabs)/financial-setup/medios-pago" as any)
          }
        >
          <Text style={styles.optionTitle}>Medios de pago</Text>
          <Text style={styles.arrow}>→</Text>
        </Pressable>
      </ScrollView>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Hola, {user.nombre}</Text>
      <Text style={styles.subtitle}>{user.mail}</Text>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Estado de cuenta</Text>
        <Text style={styles.status}>Cuenta activa</Text>
        <Text style={styles.infoText}>
          Tu cuenta está lista para operar dentro de la app.
        </Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Categoría de postor</Text>
        <Text style={styles.category}>{user.categoria ?? "PLATA"}</Text>
      </View>

      <Pressable style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>Cerrar sesión</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F5F6FA" },

  container: {
    flexGrow: 1,
    paddingHorizontal: 24,
    paddingTop: 42,
    paddingBottom: 40,
  },

  stateContainer: {
    flex: 1,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },

  pendingContainer: {
    flex: 1,
    backgroundColor: "#27447F",
    paddingHorizontal: 28,
    paddingTop: 70,
    paddingBottom: 34,
  },

  pendingTitle: {
    color: "white",
    fontSize: 27,
    fontWeight: "800",
    lineHeight: 36,
    marginBottom: 28,
  },

  pendingSubtitle: {
    color: "white",
    fontSize: 25,
    lineHeight: 34,
    marginBottom: 26,
  },

  pendingText: {
    marginTop: 10,
    color: "white",
    fontSize: 15,
  },

  mailText: {
    color: "#DBEAFE",
    fontSize: 15,
    fontWeight: "800",
    marginBottom: 38,
  },

  hourglass: {
    color: "white",
    fontSize: 78,
    textAlign: "center",
    marginBottom: 42,
  },

  transparentButton: {
    paddingVertical: 15,
    alignItems: "center",
    marginTop: 12,
  },

  transparentButtonText: {
    color: "white",
    fontSize: 15,
    fontWeight: "800",
  },

  title: {
    fontSize: 30,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 24,
  },

  card: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
  },

  cardLabel: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 8,
  },

  status: {
    color: "#16A34A",
    fontSize: 20,
    fontWeight: "900",
    marginBottom: 8,
  },

  category: {
    fontSize: 22,
    color: "#111827",
    fontWeight: "900",
  },

  infoText: {
    fontSize: 14,
    color: "#4B5563",
    lineHeight: 21,
  },

  optionCard: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },

  optionTitle: {
    fontSize: 18,
    color: "#111827",
    fontWeight: "900",
  },

  arrow: {
    fontSize: 26,
    color: "#2F63F6",
    fontWeight: "900",
  },

  primaryButton: {
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
    marginTop: 8,
  },

  primaryButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },

  secondaryButton: {
    backgroundColor: "white",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    marginBottom: 12,
  },

  secondaryButtonText: {
    color: "#111827",
    fontSize: 16,
    fontWeight: "800",
  },

  logoutButton: {
    backgroundColor: "#111827",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
  },

  logoutText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },
});
