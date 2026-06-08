import { useAuth } from "@/src/context/authContext";
import { router } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";

export default function ProfileScreen() {
  const { user, isAuthenticated, isValidated, logout } = useAuth();

  async function handleLogout() {
    await logout();
    router.replace("/auth/login");
  }

  if (!isAuthenticated || !user) {
    return (
      <View style={styles.container}>
        <Text style={styles.emoji}>👤</Text>
        <Text style={styles.title}>Tu perfil</Text>
        <Text style={styles.subtitle}>
          Iniciá sesión para ver tu información y gestionar tu cuenta.
        </Text>

        <Pressable style={styles.primaryButton} onPress={() => router.push("/auth/login")}>
          <Text style={styles.primaryButtonText}>Iniciar sesión</Text>
        </Pressable>

        <Pressable style={styles.secondaryButton} onPress={() => router.push("/auth/register")}>
          <Text style={styles.secondaryButtonText}>Crear cuenta</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.emoji}>👤</Text>

      <Text style={styles.title}>Hola, {user.nombre}</Text>

      <Text style={styles.subtitle}>{user.mail}</Text>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Estado de cuenta</Text>

        <Text style={[styles.status, isValidated ? styles.statusValid : styles.statusPending]}>
          {user.estado}
        </Text>

        {!isValidated && (
          <Text style={styles.pendingText}>
            Tu cuenta está pendiente de validación. Cuando la empresa apruebe tus
            datos, vas a poder ofertar y participar en subastas.
          </Text>
        )}
      </View>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Rol</Text>
        <Text style={styles.cardValue}>{user.rol}</Text>
      </View>

      <Pressable style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>Cerrar sesión</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F5F6FA",
    paddingHorizontal: 24,
    paddingTop: 42,
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

  cardLabel: {
    fontSize: 13,
    color: "#6B7280",
    fontWeight: "700",
    marginBottom: 8,
  },

  cardValue: {
    fontSize: 17,
    color: "#111827",
    fontWeight: "800",
  },

  status: {
    fontSize: 16,
    fontWeight: "900",
    marginBottom: 10,
  },

  statusValid: {
    color: "#16A34A",
  },

  statusPending: {
    color: "#D97706",
  },

  pendingText: {
    fontSize: 14,
    color: "#4B5563",
    lineHeight: 20,
  },

  primaryButton: {
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
    marginBottom: 12,
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
  },

  secondaryButtonText: {
    color: "#111827",
    fontSize: 16,
    fontWeight: "800",
  },

  logoutButton: {
    marginTop: 10,
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