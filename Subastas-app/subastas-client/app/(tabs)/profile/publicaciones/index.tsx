import PublicationCard from "@/src/components/publicaciones/PublicationCard";
import { publicacionesMock } from "@/src/mocks/publicacionesMock";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function MisPublicacionesScreen() {
  const activas = publicacionesMock.filter((item) => item.estado !== "VENDIDA");
  const vendidas = publicacionesMock.filter(
    (item) => item.estado === "VENDIDA",
  );

  function handleBack() {
    router.replace("/(tabs)/profile" as any);
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable onPress={handleBack} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.title}>Mis publicaciones</Text>
      </View>

      <Text style={styles.sectionTitle}>Activos</Text>
      {activas.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>No tenés publicaciones activas.</Text>
        </View>
      ) : (
        activas.map((publicacion) => (
          <PublicationCard
            key={publicacion.id}
            publicacion={publicacion}
            onPress={() =>
              router.push({
                pathname: "/(tabs)/profile/publicaciones/[id]" as any,
                params: { id: publicacion.id },
              })
            }
          />
        ))
      )}

      <Text style={styles.sectionTitle}>Vendidos</Text>
      {vendidas.map((publicacion) => (
        <PublicationCard
          key={publicacion.id}
          publicacion={publicacion}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/profile/publicaciones/[id]" as any,
              params: { id: publicacion.id },
            })
          }
        />
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: {
    paddingHorizontal: 20,
    paddingTop: 22,
    paddingBottom: 40,
  },
  headerRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 24,
  },
  iconButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },
  title: {
    fontSize: 28,
    fontWeight: "900",
    color: "#0F172A",
  },
  sectionTitle: {
    fontSize: 18,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 12,
    marginTop: 8,
  },
  emptyCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
    marginBottom: 18,
  },
  emptyText: {
    fontSize: 14,
    color: "#64748B",
    fontWeight: "700",
  },
});
