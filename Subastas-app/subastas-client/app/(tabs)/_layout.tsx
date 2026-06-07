import Ionicons from "@expo/vector-icons/Ionicons";
import { Tabs, router } from "expo-router";
import { Pressable, StyleSheet, View } from "react-native";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: true,
        headerTitle: "",
        headerStyle: {
          backgroundColor: "#2F63F6",
          height: 105,
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
          tabBarIcon: ({ color }) => (
            <Ionicons name="home-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="search"
        options={{
          tabBarIcon: ({ color }) => (
            <Ionicons name="search-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="saved"
        options={{
          tabBarIcon: ({ color }) => (
            <Ionicons name="bookmark-outline" size={26} color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="profile"
        options={{
          tabBarIcon: ({ color }) => (
            <Ionicons name="person-outline" size={26} color={color} />
          ),
        }}
      />
    </Tabs>
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
