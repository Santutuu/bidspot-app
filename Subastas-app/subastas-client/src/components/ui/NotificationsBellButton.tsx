import { useNotifications } from "@/src/context/notificationsContext";
import Ionicons from "@expo/vector-icons/Ionicons";
import { Pressable, StyleSheet, View } from "react-native";

export default function NotificationsBellButton() {
  const { unreadCount, togglePanel } = useNotifications();

  return (
    <Pressable onPress={() => void togglePanel()}>
      <View style={styles.container}>
        <Ionicons name="notifications-outline" size={34} color="white" />
        {unreadCount > 0 && <View style={styles.dot} />}
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    width: 36,
    height: 36,
    alignItems: "center",
    justifyContent: "center",
  },
  dot: {
    position: "absolute",
    top: 4,
    right: 1,
    width: 10,
    height: 10,
    borderRadius: 999,
    backgroundColor: "#111827",
    borderWidth: 1.5,
    borderColor: "#FFFFFF",
  },
});
