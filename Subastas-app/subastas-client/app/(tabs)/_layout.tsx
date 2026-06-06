import Ionicons from "@expo/vector-icons/Ionicons";
import { Link, Tabs } from "expo-router";
import { Pressable, StyleSheet, View, Text } from "react-native";


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
          <Link href="/profile" asChild>
            <Pressable style={styles.headerLeft}>
              <Ionicons name="person-circle-outline" size={34} color="white" />
            </Pressable>
          </Link>
        ),

        headerRight: () => (
          <View style={styles.headerRight}>
            <Link href="/notifications" asChild>
              <Pressable>
                <Ionicons name="notifications-outline" size={30} color="white" />
              </Pressable>
            </Link>

            <Link href="/mensajeria" asChild>
              <Pressable>
                <Ionicons name="chatbox-outline" size={30} color="white" />
              </Pressable>
            </Link>
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
      <Text style={{ fontSize: 30, color }}>⌂</Text>
    ),
  }}
/>

<Tabs.Screen
  name="search"
  options={{
    tabBarIcon: ({ color }) => (
      <Text style={{ fontSize: 30, color }}>⌕</Text>
    ),
  }}
/>

<Tabs.Screen
  name="saved"
  options={{
    tabBarIcon: ({ color }) => (
      <Text style={{ fontSize: 30, color }}>♡</Text>
    ),
  }}
/>

<Tabs.Screen
  name="profile"
  options={{
    tabBarIcon: ({ color }) => (
      <Text style={{ fontSize: 30, color }}>♙</Text>
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
    justifyContent: "center",
    alignItems: "center",
  },

  tabBarIcon: {
    marginTop: 4,
  },
});