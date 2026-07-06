import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { Pressable, StyleSheet, View } from "react-native";

export default function MessagesHeaderButton() {
  const hasUnread = true;

  return (
    <Pressable
      onPress={() => router.push("/(tabs)/mensajeria" as any)}
      hitSlop={10}
      style={styles.button}
      accessibilityRole="button"
      accessibilityLabel="Abrir mensajeria"
    >
      <Ionicons name="chatbubble-ellipses-outline" size={26} color="#FFFFFF" />
      {hasUnread ? <View style={styles.unreadDot} /> : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    width: 34,
    height: 34,
    borderRadius: 17,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(255,255,255,0.16)",
  },
  unreadDot: {
    position: "absolute",
    top: 3,
    right: 3,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: "#EF4444",
    borderWidth: 1,
    borderColor: "#FFFFFF",
  },
});
