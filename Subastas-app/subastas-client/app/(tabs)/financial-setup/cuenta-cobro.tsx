import { useCuentaCobro } from "@/src/hooks/useCuentaCobro";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback } from "react";
import {
    ActivityIndicator,
    Pressable,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

export default function CuentaCobroScreen() {
  const { from } = useLocalSearchParams<{ from?: string }>();
  const { cuenta, loading, refreshing, error, cargarCuenta, refrescar } =
    useCuentaCobro();

  const backRoute =
    from === "profile" ? "/(tabs)/profile" : "/(tabs)/financial-setup";

  useFocusEffect(
    useCallback(() => {
      void cargarCuenta();
    }, [cargarCuenta]),
  );

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={refrescar} />
      }
    >
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace(backRoute as any)}
      >
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <Text style={styles.title}>Cuenta de cobro</Text>
      <Text style={styles.subtitle}>
        La cuenta donde vas a recibir acreditaciones de tus ventas.
      </Text>

      {loading ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.stateText}>Cargando cuenta...</Text>
        </View>
      ) : error ? (
        <View style={styles.stateCard}>
          <Ionicons name="alert-circle-outline" size={30} color="#B91C1C" />
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.secondaryButton} onPress={refrescar}>
            <Text style={styles.secondaryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : cuenta ? (
        <View style={styles.accountCard}>
          <View style={styles.accountHeader}>
            <View>
              <Text style={styles.label}>CBU</Text>
              <Text style={styles.value}>{cuenta.cbu}</Text>
            </View>
            <Ionicons name="wallet-outline" size={30} color="#2F63F6" />
          </View>

          <View style={styles.detailBlock}>
            <Text style={styles.label}>Banco o billetera</Text>
            <Text style={styles.value}>{cuenta.banco}</Text>
          </View>

          <View style={styles.detailBlock}>
            <Text style={styles.label}>Titular</Text>
            <Text style={styles.value}>{cuenta.titular}</Text>
          </View>

          <Pressable
            style={styles.primaryButton}
            onPress={() =>
              router.push("/(tabs)/financial-setup/cuenta-form" as any)
            }
          >
            <Text style={styles.primaryText}>Elegir otra cuenta</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.stateCard}>
          <Ionicons name="wallet-outline" size={36} color="#2F63F6" />
          <Text style={styles.emptyTitle}>Todavia no cargaste una cuenta</Text>
          <Text style={styles.stateText}>
            Agrega una cuenta bancaria o billetera para recibir cobros.
          </Text>
          <Pressable
            style={styles.primaryButton}
            onPress={() =>
              router.push("/(tabs)/financial-setup/cuenta-form" as any)
            }
          >
            <Text style={styles.primaryText}>Agregar cuenta</Text>
          </Pressable>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  container: { paddingHorizontal: 20, paddingTop: 20, paddingBottom: 36 },
  backButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
    marginBottom: 10,
  },
  title: {
    fontSize: 28,
    fontWeight: "900",
    color: "#0F172A",
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 15,
    color: "#64748B",
    lineHeight: 22,
    marginBottom: 18,
  },
  accountCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 22,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
    shadowColor: "#0F172A",
    shadowOpacity: 0.06,
    shadowRadius: 14,
    shadowOffset: { width: 0, height: 8 },
    elevation: 2,
  },
  accountHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
    marginBottom: 18,
  },
  detailBlock: { marginBottom: 18 },
  label: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 6,
  },
  value: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "800",
    lineHeight: 24,
  },
  stateCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 22,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 20,
    alignItems: "center",
    gap: 10,
  },
  emptyTitle: {
    fontSize: 18,
    color: "#0F172A",
    fontWeight: "900",
    textAlign: "center",
  },
  stateText: {
    fontSize: 14,
    color: "#64748B",
    lineHeight: 21,
    textAlign: "center",
  },
  errorText: {
    fontSize: 14,
    color: "#B91C1C",
    lineHeight: 21,
    textAlign: "center",
    fontWeight: "700",
  },
  primaryButton: {
    marginTop: 10,
    backgroundColor: "#2F63F6",
    borderRadius: 14,
    paddingVertical: 14,
    paddingHorizontal: 18,
    alignItems: "center",
  },
  primaryText: { color: "#FFFFFF", fontSize: 15, fontWeight: "900" },
  secondaryButton: {
    marginTop: 8,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 14,
    paddingVertical: 12,
    paddingHorizontal: 16,
  },
  secondaryText: { color: "#111827", fontSize: 14, fontWeight: "900" },
});
