import { router } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";

export default function ProfileScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        Registrá tu usuario o iniciá sesión para participar
      </Text>

      <Text style={styles.subtitle}>
        Accedé a subastas, guardá favoritos y ofertá desde la app.
      </Text>

      <Pressable style={styles.primaryButton} onPress={() => router.push("/auth/login")}>
        <Text style={styles.primaryText}>Iniciar sesión</Text>
      </Pressable>

      <Pressable style={styles.secondaryButton} onPress={() => router.push("/auth/register")}>
        <Text style={styles.secondaryText}>Registrarse</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#2F63F6",
    paddingHorizontal: 26,
    justifyContent: "center",
  },

  title: {
    color: "white",
    fontSize: 30,
    fontWeight: "800",
    lineHeight: 38,
    marginBottom: 12,
  },

  subtitle: {
    color: "rgba(255,255,255,0.85)",
    fontSize: 16,
    lineHeight: 23,
    marginBottom: 34,
  },

  primaryButton: {
    backgroundColor: "white",
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: "center",
    marginBottom: 14,
  },

  primaryText: {
    color: "#2F63F6",
    fontSize: 17,
    fontWeight: "700",
  },

  secondaryButton: {
    backgroundColor: "rgba(255,255,255,0.18)",
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.35)",
  },

  secondaryText: {
    color: "white",
    fontSize: 17,
    fontWeight: "700",
  },
});