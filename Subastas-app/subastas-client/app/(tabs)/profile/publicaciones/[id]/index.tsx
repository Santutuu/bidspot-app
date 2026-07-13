import AuctionInfoCard from "@/src/components/publicaciones/AuctionInfoCard";
import PublicationStatusBadge from "@/src/components/publicaciones/PublicationStatusBadge";
import RequiredActionCard from "@/src/components/publicaciones/RequiredActionCard";
import { useDetalleSolicitudPublicacion } from "@/src/hooks/useSolicitudesPublicacion";
import { AccionSolicitudPublicacion, SolicitudPublicacionDetalle } from "@/src/types/solicitudesPublicacion";
import { derivePublicationUIState, getActionConfig } from "@/src/utils/publicationWorkflow";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useLocalSearchParams } from "expo-router";
import {
  ActivityIndicator,
  Image,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

function formatFecha(value: string | null) {
  if (!value) return "Pendiente";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("es-AR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: value.includes("T") ? "2-digit" : undefined,
    minute: value.includes("T") ? "2-digit" : undefined,
  });
}

function formatMoney(value?: number | null, moneda = "ARS") {
  if (value === null || value === undefined) return "Pendiente";
  return `${moneda} ${value.toLocaleString("es-AR")}`;
}

function actionToCard(accion: AccionSolicitudPublicacion) {
  const config = getActionConfig(accion);
  return {
    id: String(accion.idAccion),
    tipo: accion.tipo,
    titulo: accion.titulo ?? config.title,
    descripcion: accion.descripcion ?? config.description,
    icon: config.icon,
    estado: accion.estado === "RESUELTA" ? "RESPONDIDA" as const : "PENDIENTE" as const,
  };
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
  const { detalle, loading, error, recargar } = useDetalleSolicitudPublicacion(id);

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
        <Text style={styles.errorText}>{error ?? "No encontramos esta publicación."}</Text>
        <Pressable style={styles.retryButton} onPress={recargar}>
          <Text style={styles.retryText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const ui = derivePublicationUIState(detalle);
  const firstImage = detalle.imagenesUrl[0];
  const idSubasta = detalle.idSubasta;
  const tieneSubasta = idSubasta !== null && idSubasta !== undefined;
  const polizaCompletada = detalle.estado === "LISTA_PARA_SUBASTA";
  const puedeAbrirSubasta = tieneSubasta && polizaCompletada;

  function handleVerSubasta() {
    if (!puedeAbrirSubasta || !idSubasta) {
      return;
    }

    router.push({
      pathname: "/(tabs)/subastas/[id]" as any,
      params: { id: String(idSubasta) },
    });
  }

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.content}
      refreshControl={<RefreshControl refreshing={loading} onRefresh={recargar} />}
    >
      <View style={styles.headerRow}>
        <Pressable onPress={() => router.replace("/(tabs)/profile/publicaciones" as any)} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.headerTitle}>Seguimiento</Text>
      </View>

      <Text style={styles.title}>{detalle.titulo}</Text>
      <PublicationStatusBadge estado={detalle.estado} />

      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.gallery}>
        {detalle.imagenesUrl.map((image, index) => (
          <Image key={`${image}-${index}`} source={{ uri: image }} style={styles.galleryImage} />
        ))}
      </ScrollView>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Información general</Text>
        <Info label="Categoría" value={detalle.categoria} />
        <Info label="Descripción" value={detalle.descripcion} />
        <Info label="Ubicación actual" value={detalle.ubicacionActual ?? "Pendiente"} />
        <Info label="Actualizado" value={formatFecha(detalle.fechaActualizacion)} />
      </View>

      <View style={styles.card}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Acciones requeridas</Text>
          {ui.hasPendingActions && (
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
        {!ui.hasPendingActions ? (
          <Text style={styles.text}>No hay acciones pendientes. La empresa puede actualizar el estado más adelante.</Text>
        ) : (
          detalle.accionesPendientes.slice(0, 2).map((accion) => (
            <RequiredActionCard
              key={accion.idAccion}
              action={actionToCard(accion)}
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

      {ui.showShipment && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Envío para inspección</Text>
          <Info label="Depósito" value={detalle.direccionDeposito ?? "Pendiente"} />
          <Info label="Fecha límite" value={formatFecha(detalle.fechaLimiteEnvio)} />
          <Info label="Devolución con cargo" value={detalle.aceptaDevolucionConCargo ? "Aceptada" : "Pendiente"} />
        </View>
      )}

      {ui.showInspection && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Inspección</Text>
          <Info label="Recepción" value={formatFecha(detalle.fechaRecepcion)} />
          <Info label="Estado" value={detalle.estado} />
        </View>
      )}

      {ui.showConditions && detalle.propuestaVenta && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Condiciones de venta</Text>
          <Info label="Subasta" value={detalle.propuestaVenta.tituloSubasta ?? `#${detalle.propuestaVenta.idSubasta}`} />
          <Info label="Fecha" value={formatFecha(detalle.propuestaVenta.fechaSubasta)} />
          <Info label="Ubicación" value={detalle.propuestaVenta.ubicacionSubasta ?? "Pendiente"} />
          <Info label="Moneda" value={detalle.propuestaVenta.moneda ?? "Pendiente"} />
          <Info label="Precio base" value={formatMoney(detalle.propuestaVenta.precioBase, detalle.propuestaVenta.moneda ?? "ARS")} />
          <Info label="Comisión" value={detalle.propuestaVenta.porcentajeComision !== null ? `${detalle.propuestaVenta.porcentajeComision}%` : "Pendiente"} />
          <Info label="Rematador" value={detalle.propuestaVenta.rematador ?? "Pendiente"} />
        </View>
      )}

      {ui.showRejection && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Devolución</Text>
          {detalle.motivoRechazo && <Info label="Motivo rechazo" value={detalle.motivoRechazo} />}
          <Info label="Estado" value={detalle.devolucion?.estado ?? "Pendiente"} />
          <Info label="Costo" value={formatMoney(detalle.devolucion?.costo, detalle.devolucion?.moneda ?? "ARS")} />
          <Info label="Dirección" value={detalle.devolucion?.direccionDestino ?? "Pendiente"} />
          <Info label="Pago" value={formatFecha(detalle.devolucion?.fechaPago ?? null)} />
        </View>
      )}

      {tieneSubasta && (
        <View style={styles.card}>
          <AuctionInfoCard auction={auctionFromDetalle(detalle)} />
          {!puedeAbrirSubasta && (
            <Text style={styles.lockedAuctionText}>
              CompletÃ¡ la pÃ³liza para acceder a la subasta.
            </Text>
          )}
          <Pressable
            style={[
              styles.auctionButton,
              !puedeAbrirSubasta && styles.auctionButtonDisabled,
            ]}
            disabled={!puedeAbrirSubasta}
            onPress={handleVerSubasta}
          >
            <Text style={styles.auctionButtonText}>Ver subasta</Text>
            <Ionicons name="arrow-forward" size={17} color="#FFFFFF" />
          </Pressable>
        </View>
      )}

      {firstImage && ui.showFinal && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Lista para subasta</Text>
          <Text style={styles.text}>Tu artículo ya quedó preparado para participar en la subasta asociada.</Text>
        </View>
      )}
    </ScrollView>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.infoRow}>
      <Text style={styles.label}>{label}</Text>
      <Text style={styles.text}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: { paddingHorizontal: 20, paddingTop: 22, paddingBottom: 40 },
  headerRow: { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 20 },
  iconButton: { width: 42, height: 42, justifyContent: "center" },
  headerTitle: { fontSize: 18, fontWeight: "900", color: "#0F172A" },
  title: { fontSize: 28, color: "#0F172A", fontWeight: "900", lineHeight: 36, marginBottom: 12 },
  gallery: { gap: 10, paddingVertical: 18 },
  galleryImage: { width: 128, height: 110, borderRadius: 16, backgroundColor: "#E5E7EB" },
  card: { backgroundColor: "#FFFFFF", borderRadius: 18, borderWidth: 1, borderColor: "#DCE3F0", padding: 16, marginBottom: 14 },
  sectionHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 8 },
  sectionTitle: { fontSize: 17, color: "#0F172A", fontWeight: "900", marginBottom: 10 },
  infoRow: { paddingVertical: 7, borderTopWidth: 1, borderTopColor: "#EEF2F8" },
  label: { fontSize: 12, color: "#64748B", fontWeight: "900", textTransform: "uppercase", marginBottom: 4 },
  text: { fontSize: 14, color: "#475569", lineHeight: 22, fontWeight: "600" },
  linkText: { fontSize: 14, color: "#2F63F6", fontWeight: "900" },
  auctionButton: { minHeight: 46, borderRadius: 13, backgroundColor: "#2F63F6", flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6 },
  auctionButtonDisabled: { opacity: 0.45 },
  auctionButtonText: { color: "#FFFFFF", fontWeight: "900" },
  lockedAuctionText: { fontSize: 13, color: "#92400E", fontWeight: "800", lineHeight: 19, marginBottom: 10 },
  stateContainer: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24, backgroundColor: "#FFFFFF", gap: 10 },
  stateText: { fontSize: 15, color: "#64748B", fontWeight: "700" },
  errorText: { fontSize: 15, color: "#B91C1C", fontWeight: "800", textAlign: "center" },
  retryButton: { backgroundColor: "#2F63F6", borderRadius: 12, paddingHorizontal: 14, paddingVertical: 10 },
  retryText: { color: "#FFFFFF", fontWeight: "900" },
});
