import { StyleSheet, Text, View } from "react-native";

export default function MensajeriaScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Mensajería</Text>
      <Text style={styles.text}>Aquí aparecerán tus mensajes.</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    paddingHorizontal: 24,
    backgroundColor: "#FFFFFF",
  },
  title: {
    fontSize: 24,
    fontWeight: "700",
    color: "#111827",
    marginBottom: 8,
  },
  text: {
    fontSize: 15,
    color: "#4B5563",
    textAlign: "center",
  },
});
