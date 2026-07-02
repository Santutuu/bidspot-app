import Ionicons from "@expo/vector-icons/Ionicons";
import { Tabs, router } from "expo-router";
import { Image, Pressable, StyleSheet, View } from "react-native";

export default function TabsLayout() {
  return (
    <Tabs
      initialRouteName="home/index"
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
          height: 105,
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
            <Pressable
              onPress={() => router.push("/(tabs)/notifications" as any)}
            >
              <Ionicons name="notifications-outline" size={34} color="white" />
            </Pressable>
          </View>
        ),
        tabBarShowLabel: true,
        tabBarActiveTintColor: "#2F63F6",
        tabBarInactiveTintColor: "#111",
        tabBarStyle: styles.tabBar,
        tabBarItemStyle: styles.tabBarItem,
        tabBarIconStyle: styles.tabBarIcon,
        tabBarLabelStyle: styles.tabBarLabel,
      }}
    >
      <Tabs.Screen
        name="home"
        options={{
          title: "Inicio",
          tabBarIcon: ({ color }) => (
            <Ionicons name="home-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="saved"
        options={{
          title: "Guardadas",
          tabBarIcon: ({ color }) => (
            <Ionicons name="bookmark-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="search"
        options={{
          title: "Buscar",
          tabBarIcon: ({ color }) => (
            <Ionicons name="search-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="profile"
        options={{
          title: "Perfil",
          tabBarIcon: ({ color }) => (
            <Ionicons name="person-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen name="notifications" options={{ href: null }} />

      <Tabs.Screen name="auth/login" options={{ href: null }} />
      <Tabs.Screen name="auth/register" options={{ href: null }} />
      <Tabs.Screen name="auth/registration-status" options={{ href: null }} />
      <Tabs.Screen name="auth/complete-registration" options={{ href: null }} />

      <Tabs.Screen name="financial-setup/index" options={{ href: null }} />
      <Tabs.Screen
        name="financial-setup/cuenta-cobro"
        options={{ href: null }}
      />
      <Tabs.Screen
        name="financial-setup/medios-pago"
        options={{ href: null }}
      />
      <Tabs.Screen name="financial-setup/tarjeta" options={{ href: null }} />
      <Tabs.Screen name="financial-setup/cheque" options={{ href: null }} />

      <Tabs.Screen name="profile/publicaciones/index" options={{ href: null }} />
      <Tabs.Screen name="profile/publicaciones/[id]/index" options={{ href: null }} />
      <Tabs.Screen
        name="profile/publicaciones/[id]/accion/index"
        options={{ href: null }}
      />
      <Tabs.Screen name="profile/publicar/categoria" options={{ href: null }} />
      <Tabs.Screen name="profile/publicar/detalle" options={{ href: null }} />

      <Tabs.Screen name="subastas/[id]" options={{ href: null }} />
      <Tabs.Screen
        name="subastas/category/[categoria]"
        options={{ href: null }}
      />
    </Tabs>
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
  headerLeft: { marginLeft: 6 },
  headerRight: {
    flexDirection: "row",
    alignItems: "center",
    gap: 16,
    marginRight: 8,
  },
  tabBar: {
    height: 90,
    backgroundColor: "white",
    borderTopWidth: 1,
    borderTopColor: "#DDD",
    paddingBottom: 8,
  },
  tabBarItem: {
    justifyContent: "center",
    alignItems: "center",
  },
  tabBarIcon: { marginTop: 4 },
  tabBarLabel: {
    fontSize: 12,
    fontWeight: "600",
    marginBottom: 6,
  },
});
