import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text } from "react-native";

export default function MediosPagoScreen() {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Pressable
        style={styles.backButton}
        onPress={() => router.replace("/(tabs)/financial-setup" as any)}
      >
        <Text style={styles.backText}>‹</Text>
      </Pressable>

      <Text style={styles.title}>Medios de pago</Text>

      <Pressable
        style={styles.option}
        onPress={() => router.push("/(tabs)/financial-setup/tarjeta" as any)}
      >
        <Text style={styles.optionText}>Tarjetas +</Text>
      </Pressable>

      <Pressable
        style={styles.option}
        onPress={() => router.push("/(tabs)/financial-setup/cheque" as any)}
      >
        <Text style={styles.optionText}>Cheques 🧾</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#FFFFFF" },

  container: {
    paddingHorizontal: 24,
    paddingTop: 24,
    paddingBottom: 42,
  },

  backButton: {
    width: 40,
    height: 40,
    justifyContent: "center",
    marginBottom: 10,
  },

  backText: {
    fontSize: 38,
    color: "#111827",
  },

  title: {
    fontSize: 25,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 34,
  },

  option: {
    paddingVertical: 16,
  },

  optionText: {
    color: "#111827",
    fontSize: 17,
    fontWeight: "900",
  },
});
