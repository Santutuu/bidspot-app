import { useNotifications } from "@/src/context/notificationsContext";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function NotificationsPopover() {
  const { notifications, isPanelOpen, closePanel } = useNotifications();

  if (!isPanelOpen) {
    return null;
  }

  return (
    <View pointerEvents="box-none" style={styles.root}>
      <Pressable style={styles.backdrop} onPress={closePanel} />

      <View style={styles.panelWrap}>
        <View style={styles.pointer} />
        <View style={styles.panel}>
          {notifications.length === 0 ? (
            <View style={styles.emptyState}>
              <Ionicons
                name="notifications-off-outline"
                size={28}
                color="#64748B"
              />
              <Text style={styles.emptyTitle}>No tenés notificaciones</Text>
              <Text style={styles.emptyText}>
                Cuando la empresa responda una solicitud de publicación, la vas
                a ver acá.
              </Text>
            </View>
          ) : (
            <ScrollView
              style={styles.scroll}
              contentContainerStyle={styles.scrollContent}
              showsVerticalScrollIndicator={false}
            >
              {notifications.map((notification) => (
                <Pressable
                  key={notification.id}
                  style={styles.card}
                  onPress={() => {
                    closePanel();
                    router.push({
                      pathname: "/(tabs)/profile/publicaciones/[id]" as any,
                      params: { id: String(notification.solicitudId) },
                    });
                  }}
                >
                  <Text style={styles.cardBody}>{notification.body}</Text>
                  <Text style={styles.cardAction}>Acciones requeridas</Text>
                </Pressable>
              ))}
            </ScrollView>
          )}
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    ...StyleSheet.absoluteFillObject,
    zIndex: 1000,
  },
  backdrop: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "transparent",
  },
  panelWrap: {
    position: "absolute",
    top: 82,
    right: 10,
    width: 320,
    maxWidth: "92%",
  },
  pointer: {
    alignSelf: "flex-end",
    marginRight: 14,
    width: 18,
    height: 18,
    backgroundColor: "#FFFFFF",
    borderTopWidth: 2,
    borderLeftWidth: 2,
    borderColor: "#111827",
    transform: [{ rotate: "45deg" }],
  },
  panel: {
    marginTop: -9,
    backgroundColor: "#FFFFFF",
    borderRadius: 28,
    borderWidth: 2,
    borderColor: "#111827",
    maxHeight: 460,
    overflow: "hidden",
    shadowColor: "#0F172A",
    shadowOpacity: 0.18,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 12 },
    elevation: 9,
  },
  scroll: {
    flexGrow: 0,
  },
  scrollContent: {
    padding: 16,
    gap: 12,
  },
  emptyState: {
    padding: 20,
    alignItems: "center",
    gap: 10,
  },
  emptyTitle: {
    color: "#0F172A",
    fontSize: 17,
    fontWeight: "900",
    textAlign: "center",
  },
  emptyText: {
    color: "#64748B",
    fontSize: 14,
    lineHeight: 20,
    textAlign: "center",
  },
  card: {
    borderRadius: 24,
    borderWidth: 2,
    borderColor: "#111827",
    paddingHorizontal: 16,
    paddingVertical: 18,
    backgroundColor: "#FFFFFF",
  },
  cardBody: {
    color: "#111827",
    fontSize: 15,
    lineHeight: 22,
    marginBottom: 18,
  },
  cardAction: {
    color: "#2F63F6",
    fontSize: 15,
    fontWeight: "900",
    textAlign: "center",
  },
});
