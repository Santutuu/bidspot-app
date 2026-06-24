import Ionicons from "@expo/vector-icons/Ionicons";
import { Stack, router } from "expo-router";
import { Image, Pressable, StyleSheet, View } from "react-native";

import { AuthProvider } from "@/src/context/authContext";

export default function RootLayout() {
  return (
    <AuthProvider>
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
              <Ionicons name="person-circle-outline" size={38} color="white" />
            </Pressable>
          ),
          headerRight: () => (
            <View style={styles.headerRight}>
              <Pressable onPress={() => router.push("/(tabs)/notifications")}>
                <Ionicons
                  name="notifications-outline"
                  size={34}
                  color="white"
                />
              </Pressable>
            </View>
          ),
        }}
      >
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      </Stack>
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
});
