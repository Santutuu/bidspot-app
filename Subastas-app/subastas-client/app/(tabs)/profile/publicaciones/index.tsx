import PublicationCard from "@/src/components/publicaciones/PublicationCard";
import { useMisSolicitudesPublicacion } from "@/src/hooks/useSolicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import {
    ActivityIndicator,
    Pressable,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

export default function MisPublicacionesScreen() {
  const { solicitudes, loading, error, recargar } =
    useMisSolicitudesPublicacion();
  const activas = solicitudes.filter((item) => item.estado !== "CANCELADA");
  const cerradas = solicitudes.filter((item) => item.estado === "CANCELADA");

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.content}
      refreshControl={
        <RefreshControl refreshing={loading} onRefresh={recargar} />
      }
    >
      <View style={styles.headerRow}>
        <Pressable
          onPress={() => router.replace("/(tabs)/profile" as any)}
          style={styles.iconButton}
        >
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.title}>Mis publicaciones</Text>
      </View>

      {loading && solicitudes.length === 0 && (
        <View style={styles.emptyCard}>
          <ActivityIndicator color="#2F63F6" />
          <Text style={styles.emptyText}>Cargando publicaciones...</Text>
        </View>
      )}

      {!loading && error && (
        <View style={styles.emptyCard}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={recargar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      )}

      <Text style={styles.sectionTitle}>Activos</Text>
      {!loading && !error && activas.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>No tenes publicaciones activas.</Text>
        </View>
      ) : (
        activas.map((publicacion) => (
          <PublicationCard
            key={publicacion.idSolicitud}
            publicacion={publicacion}
            onPress={() =>
              router.push({
                pathname: "/(tabs)/profile/publicaciones/[id]" as any,
                params: { id: String(publicacion.idSolicitud) },
              })
            }
          />
        ))
      )}

      {cerradas.length > 0 && <Text style={styles.sectionTitle}>Cerradas</Text>}
      {cerradas.map((publicacion) => (
        <PublicationCard
          key={publicacion.idSolicitud}
          publicacion={publicacion}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/profile/publicaciones/[id]" as any,
              params: { id: String(publicacion.idSolicitud) },
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
    gap: 10,
  },
  emptyText: {
    fontSize: 14,
    color: "#64748B",
    fontWeight: "700",
  },
  errorText: {
    fontSize: 14,
    color: "#B91C1C",
    fontWeight: "800",
  },
  retryButton: {
    alignSelf: "flex-start",
    backgroundColor: "#2F63F6",
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  retryText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
});
