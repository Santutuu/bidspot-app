import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { Pressable, StyleSheet } from "react-native";

type Props = {
  onPress?: () => void;
};

export default function FlowBackButton({ onPress }: Props) {
  return (
    <Pressable style={styles.button} onPress={onPress ?? (() => router.back())}>
      <Ionicons name="chevron-back" size={22} color="#111827" />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    width: 38,
    height: 38,
    borderRadius: 19,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 18,
  },
});