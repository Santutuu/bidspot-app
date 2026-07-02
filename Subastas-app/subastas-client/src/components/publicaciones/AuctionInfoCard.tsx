import { StyleSheet, Text, View } from "react-native";

type Props = {
  auction?: {
    titulo: string;
    fecha: string;
    hora: string;
    lugar: string;
    valorBase: number;
    comision: string;
  };
};

export default function AuctionInfoCard({ auction }: Props) {
  if (!auction) {
    return (
      <View style={styles.card}>
        <Text style={styles.title}>Subasta</Text>
        <Text style={styles.empty}>No hay subasta asignada.</Text>
      </View>
    );
  }

  return (
    <View style={styles.card}>
      <Text style={styles.title}>{auction.titulo}</Text>
      <Text style={styles.text}>
        {auction.fecha} - {auction.hora}
      </Text>
      <Text style={styles.text}>{auction.lugar}</Text>
      <Text style={styles.text}>Valor base: ARS {auction.valorBase}</Text>
      <Text style={styles.text}>Comisión: {auction.comision}</Text>
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
