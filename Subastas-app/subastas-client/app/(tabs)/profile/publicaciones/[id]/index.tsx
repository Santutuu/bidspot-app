import AuctionInfoCard from "@/src/components/publicaciones/AuctionInfoCard";
import PublicationStatusBadge from "@/src/components/publicaciones/PublicationStatusBadge";
import RequiredActionCard from "@/src/components/publicaciones/RequiredActionCard";
import { useDetalleSolicitudPublicacion } from "@/src/hooks/useSolicitudesPublicacion";
import {
    AccionRequerida,
    SolicitudPublicacionDetalle,
} from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useLocalSearchParams } from "expo-router";
import {
    ActivityIndicator,
    Image,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

function accionToCard(accion: AccionRequerida) {
  const labels: Record<AccionRequerida, string> = {
    ENVIAR_ITEM: "Enviar item",
    PROPUESTA_COLECCION: "Propuesta colección",
    ACEPTAR_CONDICIONES_VENTA: "Condiciones de venta",
    ACEPTAR_POLIZA: "Póliza de subasta",
    MODIFICAR_POLIZA: "Modificar póliza",
    COMPROBAR_ORIGEN_LICITO: "Comprobar origen lícito",
  };

  return {
    id: accion,
    tipo: accion,
    titulo: labels[accion],
    descripcion: "Hay una acción pendiente para esta publicación.",
    estado: "PENDIENTE" as const,
  };
}

function formatFecha(value: string | null) {
  if (!value) return null;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("es-AR");
}

function auctionFromDetalle(detalle: SolicitudPublicacionDetalle) {
  if (!detalle.idSubasta) return undefined;

  return {
    titulo: detalle.tituloSubasta ?? `Subasta #${detalle.idSubasta}`,
    fecha: formatFecha(detalle.fechaSubasta),
    lugar: detalle.ubicacionSubasta ?? "Ubicación pendiente",
  };
}

export default function PublicacionDetalleScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { detalle, loading, error, recargar } =
    useDetalleSolicitudPublicacion(id);

  if (loading && !detalle) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator color="#2F63F6" />
        <Text style={styles.stateText}>Cargando publicación...</Text>
      </View>
    );
  }

  if (error || !detalle) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>
          {error ?? "No encontramos esta publicación."}
        </Text>
        <Pressable style={styles.retryButton} onPress={recargar}>
          <Text style={styles.retryText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable
          onPress={() => router.replace("/(tabs)/profile" as any)}
          style={styles.iconButton}
        >
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.headerTitle}>Detalle publicación</Text>
      </View>

      <Text style={styles.title}>{detalle.titulo}</Text>
      <PublicationStatusBadge estado={detalle.estado as any} />

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.gallery}
      >
        {detalle.imagenesUrl.map((image, index) => (
          <Image
            key={`${image}-${index}`}
            source={{ uri: image }}
            style={styles.galleryImage}
          />
        ))}
      </ScrollView>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Información general</Text>
        <Text style={styles.label}>Categoría</Text>
        <Text style={styles.text}>{detalle.categoria}</Text>
        <Text style={styles.label}>Descripción</Text>
        <Text style={styles.text}>{detalle.descripcion}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Estado de publicación</Text>
        <Text style={styles.text}>{detalle.estado}</Text>
      </View>

      <View style={styles.card}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Acciones requeridas</Text>
          {detalle.accionesRequeridas.length > 0 && (
            <Pressable
              onPress={() =>
                router.push({
                  pathname: "/(tabs)/profile/publicaciones/[id]/accion" as any,
                  params: { id: detalle.idSolicitud },
                })
              }
            >
              <Text style={styles.linkText}>Ver todas</Text>
            </Pressable>
          )}
        </View>

        {detalle.accionesRequeridas.length === 0 ? (
          <Text style={styles.text}>No hay acciones pendientes.</Text>
        ) : (
          detalle.accionesRequeridas.slice(0, 2).map((accion) => (
            <RequiredActionCard
              key={accion}
              action={accionToCard(accion)}
              onPress={() =>
                router.push({
                  pathname: "/(tabs)/profile/publicaciones/[id]/accion" as any,
                  params: { id: detalle.idSolicitud },
                })
              }
            />
          ))
        )}
      </View>

      {detalle.idSubasta && (
        <View style={styles.card}>
          <AuctionInfoCard auction={auctionFromDetalle(detalle)} />
          <Pressable
            style={styles.auctionButton}
            onPress={() =>
              router.push({
                pathname: "/(tabs)/subastas/[id]" as any,
                params: { id: String(detalle.idSubasta) },
              })
            }
          >
            <Text style={styles.auctionButtonText}>Ver subasta</Text>
            <Ionicons name="arrow-forward" size={17} color="#FFFFFF" />
          </Pressable>
        </View>
      )}

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Respuestas previas</Text>
        {detalle.respuestasAcciones.length === 0 ? (
          <Text style={styles.text}>Todavía no enviaste respuestas.</Text>
        ) : (
          detalle.respuestasAcciones.map((respuesta) => (
            <View key={respuesta.idRespuesta} style={styles.responseBox}>
              <Text style={styles.responseTitle}>
                {respuesta.accion} · {respuesta.tipoRespuesta}
              </Text>
              {respuesta.comentario && (
                <Text style={styles.text}>{respuesta.comentario}</Text>
              )}
              {respuesta.archivoUrl && (
                <Text style={styles.linkText}>{respuesta.archivoUrl}</Text>
              )}
              {respuesta.montoAseguradoSolicitado !== null && (
                <Text style={styles.text}>
                  Monto solicitado: ARS {respuesta.montoAseguradoSolicitado}
                </Text>
              )}
              <Text style={styles.responseDate}>
                {formatFecha(respuesta.fechaRespuesta)}
              </Text>
            </View>
          ))
        )}
      </View>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Información adicional</Text>
        {detalle.motivoRechazo && (
          <Text style={styles.text}>
            Motivo rechazo: {detalle.motivoRechazo}
          </Text>
        )}
        <Text style={styles.text}>
          Depósito: {detalle.ubicacionDeposito ?? "Sin depósito asignado"}
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
  auctionButton: {
    minHeight: 46,
    borderRadius: 13,
    backgroundColor: "#2F63F6",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
  },
  auctionButtonText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
  responseBox: {
    borderTopWidth: 1,
    borderTopColor: "#EEF2F8",
    paddingTop: 12,
    marginTop: 10,
    gap: 4,
  },
  responseTitle: {
    fontSize: 14,
    color: "#0F172A",
    fontWeight: "900",
  },
  responseDate: {
    fontSize: 12,
    color: "#94A3B8",
    fontWeight: "700",
  },
  stateContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#FFFFFF",
    gap: 10,
  },
  stateText: {
    fontSize: 15,
    color: "#64748B",
    fontWeight: "700",
  },
  errorText: {
    fontSize: 15,
    color: "#B91C1C",
    fontWeight: "800",
    textAlign: "center",
  },
  retryButton: {
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
