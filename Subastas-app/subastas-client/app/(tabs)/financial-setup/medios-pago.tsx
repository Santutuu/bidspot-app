import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function MediosPagoScreen() {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace("/(tabs)/financial-setup" as any)}
      >
        <Ionicons name="chevron-back" size={28} color="#111827" />
      </Pressable>

      <Text style={styles.title}>Medios de pago</Text>
      <Text style={styles.subtitle}>
        Administra las tarjetas y cheques disponibles para participar.
      </Text>

      <View style={styles.options}>
        <Pressable
          style={styles.optionCard}
          onPress={() => router.push("/(tabs)/financial-setup/tarjeta" as any)}
        >
          <View style={styles.iconBox}>
            <Ionicons name="card-outline" size={24} color="#2F63F6" />
          </View>

          <View style={styles.optionTextBlock}>
            <Text style={styles.optionTitle}>Tarjetas</Text>
            <Text style={styles.optionSubtitle}>Hasta 3 tarjetas</Text>
          </View>

          <Ionicons name="add-circle-outline" size={28} color="#111827" />
        </Pressable>

        <Pressable
          style={styles.optionCard}
          onPress={() => router.push("/(tabs)/financial-setup/cheque" as any)}
        >
          <View style={styles.iconBox}>
            <Ionicons name="document-text-outline" size={24} color="#2F63F6" />
          </View>

          <View style={styles.optionTextBlock}>
            <Text style={styles.optionTitle}>Cheques</Text>
            <Text style={styles.optionSubtitle}>Hasta 3 cheques</Text>
          </View>

          <Ionicons name="add-circle-outline" size={28} color="#111827" />
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  container: { paddingHorizontal: 20, paddingTop: 20, paddingBottom: 36 },
  backButton: { width: 42, height: 42, justifyContent: "center" },
  title: {
    marginTop: 10,
    marginBottom: 8,
    fontSize: 28,
    fontWeight: "900",
    color: "#0F172A",
  },
  subtitle: {
    fontSize: 15,
    color: "#64748B",
    lineHeight: 22,
    marginBottom: 18,
  },
  options: { gap: 14 },
  optionCard: {
    minHeight: 82,
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    paddingHorizontal: 16,
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
    shadowColor: "#0F172A",
    shadowOpacity: 0.04,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 5 },
    elevation: 1,
  },
  iconBox: {
    width: 48,
    height: 48,
    borderRadius: 16,
    backgroundColor: "#EFF6FF",
    alignItems: "center",
    justifyContent: "center",
  },
  optionTextBlock: { flex: 1 },
  optionTitle: { fontSize: 19, color: "#0F172A", fontWeight: "900" },
  optionSubtitle: {
    marginTop: 4,
    fontSize: 13,
    color: "#64748B",
    fontWeight: "700",
  },
});
