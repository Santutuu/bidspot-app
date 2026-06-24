import { useAuth } from "@/src/context/authContext";
import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function FinancialSetupScreen() {
  const { user } = useAuth();

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Registro financiero</Text>

      <Text style={styles.title}>Hola {user?.nombre ?? ""}</Text>

      <Text style={styles.subtitle}>
        Para completar tu registro y poder pujar, cargá una cuenta de cobro y al
        menos un medio de pago.
      </Text>

      <View style={styles.infoCard}>
        <Text style={styles.cardTitle}>Cuenta validada</Text>

        <Text style={styles.infoText}>
          Tu cuenta fue validada exitosamente.
        </Text>

        <Text style={styles.category}>
          Categoría: {user?.categoria ?? "PLATA"}
        </Text>
      </View>

      <Pressable
        style={styles.optionCard}
        onPress={() => router.push("/financial-setup/cuenta-cobro")}
      >
        <View>
          <Text style={styles.optionTitle}>Cuenta de cobro</Text>
          <Text style={styles.optionText}>
            Cargá el CBU, banco o billetera y titular.
          </Text>
        </View>

        <Text style={styles.arrow}>→</Text>
      </Pressable>

      <Pressable
        style={styles.optionCard}
        onPress={() => router.push("/financial-setup/medios-pago")}
      >
        <View>
          <Text style={styles.optionTitle}>Medios de pago</Text>
          <Text style={styles.optionText}>
            Agregá tarjetas o cheques certificados.
          </Text>
        </View>

        <Text style={styles.arrow}>→</Text>
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
    paddingHorizontal: 22,
    paddingTop: 34,
    paddingBottom: 42,
  },

  kicker: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "800",
    textTransform: "uppercase",
    letterSpacing: 0.6,
    marginBottom: 8,
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
    marginBottom: 22,
  },

  infoCard: {
    backgroundColor: "#2F63F6",
    borderRadius: 22,
    padding: 20,
    marginBottom: 18,
  },

  cardTitle: {
    color: "white",
    fontSize: 21,
    fontWeight: "900",
    marginBottom: 8,
  },

  infoText: {
    color: "#EAF0FF",
    fontSize: 15,
    lineHeight: 22,
  },

  category: {
    color: "white",
    fontSize: 17,
    fontWeight: "900",
    marginTop: 12,
  },

  optionCard: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },

  optionTitle: {
    fontSize: 18,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 6,
  },

  optionText: {
    fontSize: 14,
    color: "#6B7280",
    lineHeight: 20,
    maxWidth: 260,
  },

  arrow: {
    fontSize: 26,
    fontWeight: "800",
    color: "#2F63F6",
  },
});