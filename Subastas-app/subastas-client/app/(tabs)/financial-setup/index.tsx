import { obtenerCuentaCobro, obtenerMediosPago } from "@/src/api/meAPI";
import { useAuth } from "@/src/context/authContext";
import { router } from "expo-router";
import { useEffect, useState } from "react";
import {
    ActivityIndicator,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

export default function FinancialSetupScreen() {
  const { user, refreshUser, logout } = useAuth();

  const [hasCuenta, setHasCuenta] = useState(false);
  const [hasMedioPago, setHasMedioPago] = useState(false);
  const [loading, setLoading] = useState(true);

  async function loadStatus() {
    try {
      setLoading(true);

      try {
        await obtenerCuentaCobro();
        setHasCuenta(true);
      } catch {
        setHasCuenta(false);
      }

      try {
        const medios = await obtenerMediosPago();
        setHasMedioPago(medios.length > 0);
      } catch {
        setHasMedioPago(false);
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadStatus();
  }, []);

  async function finishIfReady() {
    await refreshUser();
    router.replace("/(tabs)/profile");
  }

  async function handleLogout() {
    await logout();
    router.replace("/(tabs)/profile");
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Hola {user?.nombre ?? ""}!</Text>

      <Text style={styles.subtitle}>
        Agregá configuración financiera para completar registro.
      </Text>

      <View style={styles.card}>
        <Text style={styles.label}>Tu categoría es</Text>
        <Text style={styles.category}>{user?.categoria ?? "PLATA"}</Text>
      </View>

      {loading ? (
        <ActivityIndicator color="#2F63F6" />
      ) : (
        <>
          <Pressable
            style={styles.option}
            onPress={() =>
              router.push("/(tabs)/financial-setup/cuenta-cobro" as any)
            }
          >
            <View>
              <Text style={styles.optionText}>Cuenta de cobro</Text>
              <Text style={styles.optionHint}>
                {hasCuenta ? "Cargada correctamente" : "Pendiente"}
              </Text>
            </View>

            <Text style={styles.arrow}>→</Text>
          </Pressable>

          <Pressable
            style={styles.option}
            onPress={() =>
              router.push("/(tabs)/financial-setup/medios-pago" as any)
            }
          >
            <View>
              <Text style={styles.optionText}>Medios de pago</Text>
              <Text style={styles.optionHint}>
                {hasMedioPago ? "Al menos uno cargado" : "Pendiente"}
              </Text>
            </View>

            <Text style={styles.arrow}>→</Text>
          </Pressable>

          {hasCuenta && hasMedioPago && (
            <Pressable style={styles.finishButton} onPress={finishIfReady}>
              <Text style={styles.finishText}>Finalizar registro</Text>
            </Pressable>
          )}

          <Pressable style={styles.logoutButton} onPress={handleLogout}>
            <Text style={styles.logoutText}>Cerrar sesión</Text>
          </Pressable>
        </>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },

  container: {
    paddingHorizontal: 24,
    paddingTop: 46,
    paddingBottom: 42,
  },

  title: {
    fontSize: 28,
    color: "#111827",
    fontWeight: "900",
    marginBottom: 24,
  },

  subtitle: {
    fontSize: 16,
    color: "#111827",
    lineHeight: 24,
    marginBottom: 28,
    maxWidth: 270,
  },

  card: {
    backgroundColor: "#EFF6FF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#BFDBFE",
    padding: 16,
    marginBottom: 24,
  },

  label: {
    color: "#2563EB",
    fontWeight: "900",
    fontSize: 12,
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 6,
  },

  category: {
    color: "#111827",
    fontWeight: "900",
    fontSize: 22,
  },

  option: {
    paddingVertical: 18,
    borderBottomWidth: 1,
    borderBottomColor: "#E5E7EB",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },

  optionText: {
    color: "#111827",
    fontSize: 19,
    fontWeight: "900",
  },

  optionHint: {
    color: "#6B7280",
    fontSize: 13,
    marginTop: 4,
  },

  arrow: {
    color: "#111827",
    fontSize: 25,
    fontWeight: "800",
  },

  finishButton: {
    marginTop: 34,
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
  },

  finishText: {
    color: "white",
    fontSize: 16,
    fontWeight: "900",
  },

  logoutButton: {
    marginTop: 14,
    backgroundColor: "#FFFFFF",
    paddingVertical: 14,
    borderRadius: 14,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#D1D5DB",
  },

  logoutText: {
    color: "#111827",
    fontSize: 15,
    fontWeight: "800",
  },
});
