import { StyleSheet, Text, View } from "react-native";

type Props = {
  poliza?: {
    empresa: string;
    numero: string;
    cobertura: number;
    prima: number;
    estado: string;
  };
};

export default function InsuranceCard({ poliza }: Props) {
  if (!poliza) {
    return (
      <View style={styles.card}>
        <Text style={styles.title}>Seguro</Text>
        <Text style={styles.empty}>No hay seguro asignado.</Text>
      </View>
    );
  }

  return (
    <View style={styles.card}>
      <Text style={styles.title}>{poliza.empresa}</Text>
      <Text style={styles.text}>Póliza: {poliza.numero}</Text>
      <Text style={styles.text}>Cobertura: ARS {poliza.cobertura}</Text>
      <Text style={styles.text}>Prima: ARS {poliza.prima}</Text>
      <Text style={styles.text}>Estado: {poliza.estado}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 16,
    marginBottom: 14,
  },
  title: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 8,
  },
  text: {
    fontSize: 14,
    color: "#475569",
    lineHeight: 22,
    fontWeight: "600",
  },
  empty: {
    fontSize: 14,
    color: "#64748B",
    fontWeight: "700",
  },
});
