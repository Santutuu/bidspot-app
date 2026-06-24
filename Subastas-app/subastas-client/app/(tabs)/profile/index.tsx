import { useAuth } from "@/src/context/authContext";
import { router } from "expo-router";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

function formatEstado(estado: string) {
  switch (estado) {
    case "PENDIENTE_VALIDACION":
      return "Pendiente de validación";
    case "VALIDADO":
      return "Cuenta activa";
    case "RECHAZADO":
      return "Solicitud rechazada";
    case "BLOQUEADO":
      return "Cuenta bloqueada";
    default:
      return estado;
  }
}

export default function ProfileScreen() {
  const {
    user,
    loadingAuth,
    isAuthenticated,
    isValidated,
    isBlocked,
    isRejected,
    logout,
    refreshUser,
  } = useAuth();

  async function handleLogout() {
    await logout();
    router.replace("/auth/login");
  }

  if (loadingAuth) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando perfil...</Text>
      </View>
    );
  }

  if (!isAuthenticated || !user) {
    return (
      <View style={styles.container}>
        <Text style={styles.emoji}>👤</Text>

        <Text style={styles.title}>Tu perfil</Text>

        <Text style={styles.subtitle}>
          Iniciá sesión para ver tu información y gestionar tu cuenta.
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

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.emoji}>👤</Text>

      <Text style={styles.title}>Hola, {user.nombre}</Text>
      <Text style={styles.subtitle}>{user.mail}</Text>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Estado de cuenta</Text>

        <Text
          style={[
            styles.status,
            isValidated && styles.statusValid,
            !isValidated && !isBlocked && !isRejected && styles.statusPending,
            (isBlocked || isRejected) && styles.statusDanger,
          ]}
        >
          {formatEstado(user.estado)}
        </Text>

        {user.estado === "PENDIENTE_VALIDACION" && (
          <Text style={styles.infoText}>
            Tu cuenta está pendiente de validación. La empresa revisará tus datos
            y, si te acepta, te habilitará para continuar el registro.
          </Text>
        )}

        {isValidated && (
          <Text style={styles.infoText}>
            Tu cuenta ya fue validada por la empresa. Para participar en subastas,
            debés cargar al menos un medio de pago.
          </Text>
        )}

        {isBlocked && (
          <Text style={styles.infoText}>
            Tu cuenta se encuentra bloqueada. No podés operar dentro de la app.
          </Text>
        )}

        {isRejected && (
          <Text style={styles.infoText}>
            Tu solicitud fue rechazada. No podés completar el registro.
          </Text>
        )}
      </View>

      {isValidated && (
        <View style={styles.card}>
          <Text style={styles.cardLabel}>Categoría de postor</Text>

          <Text style={styles.category}>Tu categoría es: PLATA</Text>

          <Text style={styles.infoText}>
            Esta categoría determina en qué subastas podés participar. Las
            categorías disponibles son común, especial, plata, oro y platino.
          </Text>
        </View>
      )}

      {isValidated && (
        <View style={styles.paymentCard}>
          <Text style={styles.cardLabel}>Registro pendiente</Text>

          <Text style={styles.paymentTitle}>Cargá cuenta y medio de pago</Text>

          <Text style={styles.infoText}>
            Para completar tu registro debés cargar al menos un medio de pago:
            cuenta bancaria, tarjeta de crédito o cheque certificado.
          </Text>

          <Pressable
            style={styles.primaryButton}
            onPress={() => router.push("/financial-setup/medios-pago")}
          >
            <Text style={styles.primaryButtonText}>
              Cargar cuenta y medio de pago
            </Text>
          </Pressable>
        </View>
      )}

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Rol</Text>
        <Text style={styles.cardValue}>{user.rol}</Text>
      </View>

      <Pressable style={styles.secondaryButton} onPress={refreshUser}>
        <Text style={styles.secondaryButtonText}>Actualizar estado</Text>
      </Pressable>

      <Pressable style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>Cerrar sesión</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F5F6FA",
  },

  container: {
    flexGrow: 1,
    paddingHorizontal: 24,
    paddingTop: 42,
    paddingBottom: 40,
  },

  stateContainer: {
    flex: 1,
    backgroundColor: "#F5F6FA",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },

  stateText: {
    marginTop: 10,
    fontSize: 15,
    color: "#6B7280",
  },

  emoji: {
    fontSize: 34,
    marginBottom: 10,
  },

  title: {
    fontSize: 30,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 8,
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

  paymentCard: {
    backgroundColor: "#EFF6FF",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#BFDBFE",
    marginBottom: 14,
  },

  cardLabel: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "800",
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 8,
  },

  cardValue: {
    fontSize: 17,
    color: "#111827",
    fontWeight: "800",
  },

  status: {
    fontSize: 18,
    fontWeight: "900",
    marginBottom: 10,
  },

  statusValid: {
    color: "#16A34A",
  },

  statusPending: {
    color: "#D97706",
  },

  statusDanger: {
    color: "#B91C1C",
  },

  category: {
    fontSize: 20,
    color: "#111827",
    fontWeight: "900",
    marginBottom: 8,
  },

  paymentTitle: {
    fontSize: 20,
    color: "#111827",
    fontWeight: "900",
    marginBottom: 8,
  },

  infoText: {
    fontSize: 14,
    color: "#4B5563",
    lineHeight: 21,
    marginBottom: 12,
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
    marginTop: 4,
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