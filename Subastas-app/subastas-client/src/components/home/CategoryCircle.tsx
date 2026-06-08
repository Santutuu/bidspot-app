import Ionicons from "@expo/vector-icons/Ionicons";
import { Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  name: string;
  onPress?: () => void;
};

function getIcon(name: string) {
  switch (name) {
    case "Arte": return "brush-outline";
    case "Joyas": return "medal-outline";
    case "Vehículos": return "car-outline";
    case "Ropa": return "bag-handle-outline";
    default: return "cube-outline";
  }
}

export default function CategoryCircle({ name, onPress }: Props) {
  return (
    <Pressable style={styles.container} onPress={onPress}>
      <View style={styles.circle}>
        <Ionicons
          name={getIcon(name)}
          size={30}
          color="#2F63F6" // Color azul fuerte para el icono
        />
      </View>

      <Text style={styles.label}>{name}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    width: 90,
    marginHorizontal: 6,
  },

  circle: {
    width: 74,
    height: 74,
    borderRadius: 37,

    // Azul muy tenue (casi blanco)
    backgroundColor: "#F0F5FF", 

    // Borde marcado en azul
    borderWidth: 2,
    borderColor: "#2F63F6", 

    justifyContent: "center",
    alignItems: "center",

    // Sombra sutil
    shadowColor: "#2F63F6",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },

  label: {
    marginTop: 8,
    fontSize: 13,
    textAlign: "center",
    color: "#333",
    lineHeight: 16,
    fontWeight: "500",
  },
});