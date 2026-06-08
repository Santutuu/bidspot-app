import Ionicons from "@expo/vector-icons/Ionicons";
import { Tabs, router } from "expo-router";
import { Image, Pressable, StyleSheet, View } from "react-native";

export default function TabsLayout() {
  return (
    <Tabs
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
            <Pressable onPress={() => router.push("/(tabs)/notifications")}>
              <Ionicons name="notifications-outline" size={34} color="white" />
            </Pressable>

            <Pressable onPress={() => router.push("/mensajeria")}>
              <Ionicons name="chatbox-outline" size={34} color="white" />
            </Pressable>
          </View>
        ),

        tabBarShowLabel: false,
        tabBarActiveTintColor: "#2F63F6",
        tabBarInactiveTintColor: "#111",

        tabBarStyle: styles.tabBar,
        tabBarItemStyle: styles.tabBarItem,
        tabBarIconStyle: styles.tabBarIcon,
      }}
    >
      <Tabs.Screen
        name="home"
        options={{
          tabBarIcon: ({ focused }) => (
            <View style={{ 
              width: 30, 
              height: 30, 
              alignItems: 'center', 
              justifyContent: 'center' 
            }}>
              <Text style={{ fontSize: 24, opacity: focused ? 1 : 0.5 }}>🏠</Text>
            </View>
          ),
        }}
      />

      <Tabs.Screen
        name="search"
        options={{
          tabBarIcon: ({ focused }) => (
            <View style={{ 
              width: 30, 
              height: 30, 
              alignItems: 'center', 
              justifyContent: 'center' 
            }}>
              <Text style={{ fontSize: 24, opacity: focused ? 1 : 0.5 }}>🔍</Text>
            </View>
          ),
        }}
      />

      <Tabs.Screen
        name="saved"
        options={{
          tabBarIcon: ({ focused }) => (
            <View style={{ 
              width: 30, 
              height: 30, 
              alignItems: 'center', 
              justifyContent: 'center' 
            }}>
              <Text style={{ fontSize: 24, opacity: focused ? 1 : 0.5 }}>🔖</Text>
            </View>
          ),
        }}
      />

      <Tabs.Screen
        name="profile"
        options={{
          tabBarIcon: ({ focused }) => (
            <View style={{ 
              width: 30, 
              height: 30, 
              alignItems: 'center', 
              justifyContent: 'center' 
            }}>
              <Text style={{ fontSize: 24, opacity: focused ? 1 : 0.5 }}>👤</Text>
            </View>
          ),
        }}
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

  headerLeft: {
    marginLeft: 6,
  },

  headerRight: {
    flexDirection: "row",
    alignItems: "center",
    gap: 16,
    marginRight: 8,
  },

  tabBar: {
    height: 110,
    backgroundColor: "white",
    borderTopWidth: 1,
    borderTopColor: "#DDD",

    paddingBottom: 20,
  },

  tabBarItem: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  tabBarIcon: {
    marginTop: 4,
  },
});
