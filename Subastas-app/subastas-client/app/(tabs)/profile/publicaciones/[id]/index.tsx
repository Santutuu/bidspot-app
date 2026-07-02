import AuctionInfoCard from "@/src/components/publicaciones/AuctionInfoCard";
import InsuranceCard from "@/src/components/publicaciones/InsuranceCard";
import PublicationStatusBadge from "@/src/components/publicaciones/PublicationStatusBadge";
import RequiredActionCard from "@/src/components/publicaciones/RequiredActionCard";
import { getPublicacionMock } from "@/src/mocks/publicacionesMock";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useLocalSearchParams } from "expo-router";
import { Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function PublicacionDetalleScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const publicacion = getPublicacionMock(id);

  if (!publicacion) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>No encontramos esta publicación.</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable onPress={() => router.back()} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.headerTitle}>Detalle publicación</Text>
      </View>

      <Text style={styles.title}>{publicacion.titulo}</Text>
      <PublicationStatusBadge estado={publicacion.estado} />

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.gallery}
      >
        {publicacion.imagenes.map((image, index) => (
          <Image key={index} source={image} style={styles.galleryImage} />
        ))}
      </ScrollView>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Información general</Text>
        <Text style={styles.label}>Categoría</Text>
        <Text style={styles.text}>{publicacion.categoria}</Text>
        <Text style={styles.label}>Descripción</Text>
        <Text style={styles.text}>{publicacion.descripcion}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Estado de publicación</Text>
        <Text style={styles.text}>{publicacion.explicacionEstado}</Text>
      </View>

      <View style={styles.card}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Acciones requeridas</Text>
          {publicacion.acciones.length > 0 && (
            <Pressable
              onPress={() =>
                router.push({
                  pathname: "/(tabs)/profile/publicaciones/[id]/accion" as any,
                  params: { id: publicacion.id },
                })
              }
            >
              <Text style={styles.linkText}>Ver todas</Text>
            </Pressable>
          )}
        </View>

        {publicacion.acciones.length === 0 ? (
          <Text style={styles.text}>No hay acciones pendientes.</Text>
        ) : (
          publicacion.acciones.slice(0, 2).map((action) => (
            <RequiredActionCard
              key={action.id}
              action={action}
              onPress={() =>
                router.push({
                  pathname: "/(tabs)/profile/publicaciones/[id]/accion" as any,
                  params: { id: publicacion.id },
                })
              }
            />
          ))
        )}
      </View>

      <AuctionInfoCard auction={publicacion.subasta} />
      <InsuranceCard poliza={publicacion.poliza} />

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Información adicional</Text>
        {publicacion.motivoRechazo && (
          <Text style={styles.text}>Motivo rechazo: {publicacion.motivoRechazo}</Text>
        )}
        <Text style={styles.text}>
          Depósito: {publicacion.ubicacionDeposito ?? "Sin depósito asignado"}
        </Text>
        <Text style={styles.text}>
          Recepción: {publicacion.fechaRecepcion ?? "Pendiente"}
        </Text>
      </View>
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
    marginBottom: 20,
  },
  iconButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "900",
    color: "#0F172A",
  },
  title: {
    fontSize: 28,
    color: "#0F172A",
    fontWeight: "900",
    lineHeight: 36,
    marginBottom: 12,
  },
  gallery: {
    gap: 10,
    paddingVertical: 18,
  },
  galleryImage: {
    width: 128,
    height: 110,
    borderRadius: 16,
    backgroundColor: "#E5E7EB",
  },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 16,
    marginBottom: 14,
  },
  sectionHeader: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 8,
  },
  sectionTitle: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 10,
  },
  label: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    marginTop: 8,
    marginBottom: 4,
  },
  text: {
    fontSize: 14,
    color: "#475569",
    lineHeight: 22,
    fontWeight: "600",
  },
  linkText: {
    fontSize: 14,
    color: "#2F63F6",
    fontWeight: "900",
  },
  stateContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#FFFFFF",
  },
  errorText: {
    fontSize: 15,
    color: "#B91C1C",
    fontWeight: "800",
  },
});
