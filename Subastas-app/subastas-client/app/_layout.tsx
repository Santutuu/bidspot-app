import Ionicons from "@expo/vector-icons/Ionicons";
import { Stack, router } from "expo-router";
import { Pressable, StyleSheet, View } from "react-native";

import { AuthProvider } from "@/src/context/authContext";

export default function RootLayout() {
  return (
    <AuthProvider>
      <Stack
        screenOptions={{
          headerShown: true,
          headerTitle: "",
          headerStyle: {
            backgroundColor: "#2F63F6",
          },
          headerShadowVisible: false,
          headerLeft: () => (
            <Pressable
              style={styles.headerLeft}
              onPress={() => router.push("/(tabs)/profile")}
            >
              <Ionicons name="person-circle-outline" size={34} color="white" />
            </Pressable>
          ),
          headerRight: () => (
            <View style={styles.headerRight}>
              <Pressable onPress={() => router.push("/(tabs)/notifications")}>
                <Ionicons name="notifications-outline" size={30} color="white" />
              </Pressable>

              <Pressable onPress={() => router.push("/mensajeria/index")}>
                <Ionicons name="chatbox-outline" size={30} color="white" />
              </Pressable>
            </View>
          ),
        }}
      >
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />

        <Stack.Screen name="auth/login" options={{ headerShown: false }} />
        <Stack.Screen name="auth/register" options={{ headerShown: false }} />

        <Stack.Screen
          name="subastas/category/[categoria]"
          options={{ headerShown: true }}
        />

        <Stack.Screen
          name="subastas/[id]"
          options={{ headerShown: true }}
        />
      </Stack>
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  headerLeft: {
    marginLeft: 22,
  },

  headerRight: {
    flexDirection: "row",
    alignItems: "center",
    gap: 24,
    marginRight: 24,
  },
});