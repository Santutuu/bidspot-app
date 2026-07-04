import { AccionRequerida } from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  action: {
    id: string;
    tipo: AccionRequerida;
    titulo: string;
    descripcion: string;
    estado: "PENDIENTE" | "RESPONDIDA";
  };
  onPress?: () => void;
};

export default function RequiredActionCard({ action, onPress }: Props) {
  return (
    <Pressable style={styles.card} onPress={onPress}>
      <View style={styles.iconBox}>
        <Ionicons name="alert-circle-outline" size={22} color="#D97706" />
      </View>

      <View style={styles.content}>
        <Text style={styles.title}>{action.titulo}</Text>
        <Text style={styles.description}>{action.descripcion}</Text>
      </View>

      <Ionicons name="chevron-forward" size={18} color="#64748B" />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 14,
    marginBottom: 12,
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },
  iconBox: {
    width: 38,
    height: 38,
    borderRadius: 19,
    backgroundColor: "#FEF3C7",
    alignItems: "center",
    justifyContent: "center",
  },
  content: {
    flex: 1,
  },
  title: {
    fontSize: 15,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 4,
  },
  description: {
    fontSize: 13,
    color: "#64748B",
    lineHeight: 18,
    fontWeight: "600",
  },
});
