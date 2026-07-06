import Ionicons from "@expo/vector-icons/Ionicons";
import { Stack, router } from "expo-router";
import {
    Image,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

import MessagesHeaderButton from "../src/components/ui/MessagesHeaderButton";
import NotificationsBellButton from "../src/components/ui/NotificationsBellButton";
import { AuthProvider } from "../src/context/authContext";
import {
    NotificationsProvider,
    useNotifications,
} from "../src/context/notificationsContext";
import { RealtimeProvider } from "../src/context/realtimeContext";
import { SubastasGuardadasProvider } from "../src/context/subastasGuardadasContext";

function NotificationsPopover() {
  const { notifications, isPanelOpen, closePanel, dismissNotification } =
    useNotifications();

  if (!isPanelOpen) {
    return null;
  }

  return (
    <View pointerEvents="box-none" style={styles.popoverRoot}>
      <Pressable style={styles.popoverBackdrop} onPress={closePanel} />

      <View style={styles.popoverWrap}>
        <View style={styles.popoverPointer} />
        <View style={styles.popoverPanel}>
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
              style={styles.popoverScroll}
              contentContainerStyle={styles.popoverScrollContent}
              showsVerticalScrollIndicator={false}
            >
              {notifications.map((notification) => (
                <Pressable
                  key={notification.id}
                  style={styles.notificationCard}
                  onPress={async () => {
                    closePanel();
                    await dismissNotification(notification.id);
                    if (
                      notification.kind === "PUJA_SUPERADA" &&
                      notification.subastaId
                    ) {
                      router.push({
                        pathname: "/(tabs)/subastas/[id]" as any,
                        params: { id: String(notification.subastaId) },
                      });
                      return;
                    }

                    if (notification.kind === "SUBASTA_GANADA") {
                      router.push("/(tabs)/mensajeria" as any);
                      return;
                    }

                    if (notification.solicitudId) {
                      router.push({
                        pathname: "/(tabs)/profile/publicaciones/[id]" as any,
                        params: { id: String(notification.solicitudId) },
                      });
                    }
                  }}
                >
                  <Text style={styles.notificationBody}>
                    {notification.body}
                  </Text>
                  <Text style={styles.notificationAction}>
                    {notification.actionLabel ?? "Acciones requeridas"}
                  </Text>
                </Pressable>
              ))}
            </ScrollView>
          )}
        </View>
      </View>
    </View>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <SubastasGuardadasProvider>
        <RealtimeProvider>
          <NotificationsProvider>
            <Stack
              screenOptions={{
                headerShown: true,
                headerTitle: () => (
                  <Image
                    source={require("@/src/assets/images/logo-minimalista.png")}
                    style={styles.logo}
                    resizeMode="contain"
                  />
                ),
                headerTitleAlign: "left",
                headerStyle: {
                  backgroundColor: "rgba(47, 99, 246, 0.88)",
                },
                headerShadowVisible: false,
                headerLeft: () => (
                  <Pressable
                    style={styles.headerLeft}
                    onPress={() => router.push("/(tabs)/profile")}
                  >
                    <Ionicons
                      name="person-circle-outline"
                      size={38}
                      color="white"
                    />
                  </Pressable>
                ),
                headerRight: () => (
                  <View style={styles.headerRight}>
                    <MessagesHeaderButton />
                    <NotificationsBellButton />
                  </View>
                ),
              }}
            >
              <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
            </Stack>
            <NotificationsPopover />
          </NotificationsProvider>
        </RealtimeProvider>
      </SubastasGuardadasProvider>
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  logo: {
    width: 360,
    height: 104,
    marginLeft: -32,
    shadowColor: "#FFFFFF",
    shadowOpacity: 0.75,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 0 },
  },

  headerLeft: {
    marginLeft: 6,
  },

  headerRight: {
    flexDirection: "row",
    alignItems: "center",
    gap: 16,
    marginRight: 8,
  },
  popoverRoot: {
    ...StyleSheet.absoluteFillObject,
    zIndex: 1000,
  },
  popoverBackdrop: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "transparent",
  },
  popoverWrap: {
    position: "absolute",
    top: 82,
    right: 10,
    width: 272,
    maxWidth: "82%",
  },
  popoverPointer: {
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
  popoverPanel: {
    marginTop: -9,
    backgroundColor: "#FFFFFF",
    borderRadius: 24,
    borderWidth: 2,
    borderColor: "#111827",
    maxHeight: 360,
    overflow: "hidden",
    shadowColor: "#0F172A",
    shadowOpacity: 0.18,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 12 },
    elevation: 9,
  },
  popoverScroll: {
    flexGrow: 0,
  },
  popoverScrollContent: {
    paddingVertical: 10,
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
  notificationCard: {
    paddingHorizontal: 16,
    paddingVertical: 14,
    backgroundColor: "#FFFFFF",
    borderBottomWidth: 1,
    borderBottomColor: "#E5E7EB",
  },
  notificationBody: {
    color: "#111827",
    fontSize: 13,
    lineHeight: 19,
    marginBottom: 12,
  },
  notificationAction: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "900",
    textAlign: "center",
  },
});
