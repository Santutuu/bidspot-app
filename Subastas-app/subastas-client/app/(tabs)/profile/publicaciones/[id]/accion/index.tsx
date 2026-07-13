import { uploadSubastaImages } from "@/src/api/uploadAPI";
import {
  useDetalleSolicitudPublicacion,
  useDevolucionSolicitud,
  useResponderAccionSolicitud,
} from "@/src/hooks/useSolicitudesPublicacion";
import { AccionSolicitudPublicacion, ResponderAccionRequest } from "@/src/types/solicitudesPublicacion";
import { getActionConfig } from "@/src/utils/publicationWorkflow";
import Ionicons from "@expo/vector-icons/Ionicons";
import * as ImagePicker from "expo-image-picker";
import { router, useLocalSearchParams } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

function formatFecha(value: string | null) {
  if (!value) return "Pendiente";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("es-AR", { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

function formatMoney(value?: number | null, moneda = "ARS") {
  if (value === null || value === undefined) return "Pendiente";
  return `${moneda} ${value.toLocaleString("es-AR")}`;
}

export default function AccionesRequeridasScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { detalle, loading, error, recargar, setDetalle } = useDetalleSolicitudPublicacion(id);
  const { responder, loading: responding } = useResponderAccionSolicitud();
  const { configurar, confirmarPago, loading: returning } = useDevolucionSolicitud();
  const [comentarios, setComentarios] = useState<Record<string, string>>({});
  const [archivoUrls, setArchivoUrls] = useState<Record<string, string>>({});
  const [devolucionDireccion, setDevolucionDireccion] = useState("");
  const [devolucionMedioPago, setDevolucionMedioPago] = useState("");
  const [uploadingAction, setUploadingAction] = useState<number | null>(null);

  async function submit(accion: AccionSolicitudPublicacion, request: ResponderAccionRequest) {
    try {
      const updated = await responder(id, accion.idAccion, request);
      if (updated) setDetalle(updated);
      setComentarios((current) => ({ ...current, [accion.idAccion]: "" }));
      setArchivoUrls((current) => ({ ...current, [accion.idAccion]: "" }));
      await recargar();
    } catch (err: any) {
      Alert.alert("No pudimos responder", err.response?.data?.message ?? err.response?.data?.error ?? "Intentá nuevamente.");
    }
  }

  async function pickArchivo(accion: AccionSolicitudPublicacion) {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.85,
      selectionLimit: 1,
    });
    if (result.canceled || !result.assets[0]?.uri) return;
    try {
      setUploadingAction(accion.idAccion);
      const [url] = await uploadSubastaImages([result.assets[0].uri]);
      setArchivoUrls((current) => ({ ...current, [accion.idAccion]: url }));
    } catch {
      Alert.alert("Error", "No pudimos subir el archivo.");
    } finally {
      setUploadingAction(null);
    }
  }

  async function submitDevolucion() {
    const idMedioPago = Number(devolucionMedioPago);
    if (!devolucionDireccion.trim() || !Number.isFinite(idMedioPago)) {
      Alert.alert("Datos requeridos", "Ingresá dirección e id de medio de pago.");
      return;
    }
    try {
      const updated = await configurar(id, {
        direccionDestino: devolucionDireccion.trim(),
        idMedioPago,
      });
      if (updated) setDetalle(updated);
      await recargar();
    } catch (err: any) {
      Alert.alert("No pudimos configurar devolución", err.response?.data?.message ?? "Intentá nuevamente.");
    }
  }

  async function submitPagoDevolucion() {
    try {
      const updated = await confirmarPago(id);
      if (updated) setDetalle(updated);
      await recargar();
    } catch (err: any) {
      Alert.alert("No pudimos confirmar pago", err.response?.data?.message ?? "Intentá nuevamente.");
    }
  }

  if (loading && !detalle) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator color="#2F63F6" />
        <Text style={styles.description}>Cargando acciones...</Text>
      </View>
    );
  }

  if (error || !detalle) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>{error ?? "No pudimos cargar acciones."}</Text>
        <Pressable style={styles.primaryAction} onPress={recargar}>
          <Text style={styles.primaryActionText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const acciones = detalle.accionesPendientes;

  return (
    <ScrollView
      style={styles.screen}
      contentContainerStyle={styles.content}
      refreshControl={<RefreshControl refreshing={loading} onRefresh={recargar} />}
    >
      <View style={styles.headerRow}>
        <Pressable
          onPress={() =>
            router.replace({
              pathname: "/(tabs)/profile/publicaciones/[id]" as any,
              params: { id },
            })
          }
          style={styles.iconButton}
        >
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.title}>Acciones requeridas</Text>
      </View>

      {acciones.length === 0 ? (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Sin acciones pendientes</Text>
          <Text style={styles.description}>Esta publicación no requiere respuestas por ahora.</Text>
        </View>
      ) : (
        acciones.map((accion, index) => {
          const config = getActionConfig(accion);
          const comentario = comentarios[accion.idAccion] ?? "";
          const archivoUrl = archivoUrls[accion.idAccion] ?? "";
          const busy = responding || returning || uploadingAction === accion.idAccion;

          return (
            <View key={accion.idAccion} style={styles.card}>
              <Text style={styles.cardTitle}>{index + 1}. {accion.titulo ?? config.title}</Text>
              <Text style={styles.description}>{accion.descripcion ?? config.description}</Text>

              {accion.tipo === "ACEPTAR_ENVIO_INSPECCION" && (
                <>
                  <Info label="Depósito" value={detalle.direccionDeposito ?? "Pendiente"} />
                  <Info label="Fecha límite" value={formatFecha(detalle.fechaLimiteEnvio)} />
                  <Text style={styles.noticeText}>Al confirmar, aceptás enviar el producto para inspección. Si luego corresponde devolución, puede quedar a tu cargo.</Text>
                  <TextInput style={styles.input} placeholder="Comentario opcional" placeholderTextColor="#94A3B8" value={comentario} onChangeText={(value) => setComentarios((current) => ({ ...current, [accion.idAccion]: value }))} />
                  <Pressable style={styles.primaryAction} disabled={busy} onPress={() => submit(accion, { aceptada: true, comentario: comentario || undefined })}>
                    <Text style={styles.primaryActionText}>{busy ? "Enviando..." : "Confirmar envío"}</Text>
                  </Pressable>
                </>
              )}

              {accion.tipo === "ACEPTAR_CONDICIONES_VENTA" && detalle.propuestaVenta && (
                <>
                  <Info label="Subasta" value={detalle.propuestaVenta.tituloSubasta ?? `#${detalle.propuestaVenta.idSubasta}`} />
                  <Info label="Fecha" value={formatFecha(detalle.propuestaVenta.fechaSubasta)} />
                  <Info label="Ubicación" value={detalle.propuestaVenta.ubicacionSubasta ?? "Pendiente"} />
                  <Info label="Moneda" value={detalle.propuestaVenta.moneda ?? "Pendiente"} />
                  <Info label="Categoría mínima" value={detalle.propuestaVenta.categoriaMinima ?? "Pendiente"} />
                  <Info label="Rematador" value={detalle.propuestaVenta.rematador ?? "Pendiente"} />
                  <Info label="Precio base" value={formatMoney(detalle.propuestaVenta.precioBase, detalle.propuestaVenta.moneda ?? "ARS")} />
                  <Info label="Comisión" value={detalle.propuestaVenta.porcentajeComision !== null ? `${detalle.propuestaVenta.porcentajeComision}%` : "Pendiente"} />
                  <View style={styles.actionRow}>
                    <Pressable style={styles.acceptButton} disabled={busy} onPress={() => submit(accion, { aceptada: true })}>
                      <Text style={styles.acceptText}>Aceptar</Text>
                    </Pressable>
                    <Pressable style={styles.rejectButton} disabled={busy} onPress={() => submit(accion, { aceptada: false, comentario: "No acepto las condiciones." })}>
                      <Text style={styles.rejectText}>Rechazar</Text>
                    </Pressable>
                  </View>
                </>
              )}

              {accion.tipo === "REVISAR_POLIZA" && (
                <Pressable
                  style={styles.primaryAction}
                  onPress={() =>
                    router.push({
                      pathname: "/(tabs)/profile/publicaciones/[id]/poliza" as any,
                      params: { id },
                    })
                  }
                >
                  <Text style={styles.primaryActionText}>Revisar póliza</Text>
                </Pressable>
              )}

              {accion.tipo === "PAGAR_DEVOLUCION" && (
                <>
                  <Info label="Estado" value={detalle.devolucion?.estado ?? "Pendiente"} />
                  <Info label="Costo" value={formatMoney(detalle.devolucion?.costo, detalle.devolucion?.moneda ?? "ARS")} />
                  <TextInput style={styles.input} placeholder="Dirección de devolución" placeholderTextColor="#94A3B8" value={devolucionDireccion} onChangeText={setDevolucionDireccion} />
                  <TextInput style={styles.input} placeholder="Id medio de pago" placeholderTextColor="#94A3B8" keyboardType="numeric" value={devolucionMedioPago} onChangeText={setDevolucionMedioPago} />
                  <Pressable style={styles.secondaryAction} onPress={() => router.push("/(tabs)/financial-setup/medios-pago" as any)}>
                    <Text style={styles.secondaryActionText}>Ir a medios de pago</Text>
                  </Pressable>
                  <Pressable style={styles.primaryAction} disabled={busy} onPress={submitDevolucion}>
                    <Text style={styles.primaryActionText}>Configurar devolución</Text>
                  </Pressable>
                  <Pressable style={styles.primaryAction} disabled={busy} onPress={submitPagoDevolucion}>
                    <Text style={styles.primaryActionText}>Confirmar pago</Text>
                  </Pressable>
                </>
              )}

              {accion.tipo === "COMPROBAR_ORIGEN_LICITO" && (
                <>
                  <Pressable style={styles.uploadBox} onPress={() => pickArchivo(accion)} disabled={busy}>
                    <Ionicons name="document-attach-outline" size={24} color="#2F63F6" />
                    <Text style={styles.uploadText}>{archivoUrl ? "Archivo cargado" : "Añadir archivo"}</Text>
                  </Pressable>
                  <TextInput style={styles.input} placeholder="Comentario opcional" placeholderTextColor="#94A3B8" value={comentario} onChangeText={(value) => setComentarios((current) => ({ ...current, [accion.idAccion]: value }))} />
                  <Pressable style={[styles.primaryAction, !archivoUrl && styles.disabled]} disabled={busy || !archivoUrl} onPress={() => submit(accion, { archivoUrl, comentario: comentario || undefined })}>
                    <Text style={styles.primaryActionText}>Enviar comprobante</Text>
                  </Pressable>
                </>
              )}

              {accion.tipo === "PROPUESTA_COLECCION" && (
                <View style={styles.actionRow}>
                  <Pressable style={styles.acceptButton} disabled={busy} onPress={() => submit(accion, { aceptada: true })}>
                    <Text style={styles.acceptText}>Aceptar</Text>
                  </Pressable>
                  <Pressable style={styles.rejectButton} disabled={busy} onPress={() => submit(accion, { aceptada: false })}>
                    <Text style={styles.rejectText}>Rechazar</Text>
                  </Pressable>
                </View>
              )}
            </View>
          );
        })
      )}
    </ScrollView>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.infoRow}>
      <Text style={styles.policyLabel}>{label}</Text>
      <Text style={styles.policyValue}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: { paddingHorizontal: 20, paddingTop: 22, paddingBottom: 40 },
  headerRow: { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 22 },
  iconButton: { width: 42, height: 42, justifyContent: "center" },
  title: { fontSize: 26, color: "#0F172A", fontWeight: "900", flex: 1 },
  card: { backgroundColor: "#FFFFFF", borderRadius: 18, borderWidth: 1, borderColor: "#DCE3F0", padding: 16, marginBottom: 14 },
  cardTitle: { fontSize: 17, color: "#0F172A", fontWeight: "900", marginBottom: 8 },
  description: { fontSize: 14, color: "#475569", lineHeight: 22, fontWeight: "600", marginBottom: 12 },
  noticeText: { color: "#334155", fontSize: 13, fontWeight: "700", lineHeight: 19, marginBottom: 10 },
  infoRow: { flexDirection: "row", justifyContent: "space-between", gap: 12, paddingVertical: 7, borderTopWidth: 1, borderTopColor: "#EEF2F8" },
  policyLabel: { flex: 1, color: "#64748B", fontSize: 13, fontWeight: "800" },
  policyValue: { flex: 1, color: "#0F172A", fontSize: 13, fontWeight: "900", textAlign: "right" },
  primaryAction: { backgroundColor: "#111827", borderRadius: 12, paddingVertical: 13, alignItems: "center", marginTop: 10 },
  primaryActionText: { color: "#FFFFFF", fontWeight: "900" },
  secondaryAction: { borderRadius: 12, borderWidth: 1, borderColor: "#111827", paddingVertical: 12, alignItems: "center", marginTop: 10 },
  secondaryActionText: { color: "#111827", fontWeight: "900" },
  uploadBox: { minHeight: 92, borderRadius: 14, borderWidth: 1.5, borderStyle: "dashed", borderColor: "#93C5FD", alignItems: "center", justifyContent: "center", gap: 8, marginBottom: 12 },
  uploadText: { color: "#2F63F6", fontWeight: "900" },
  actionRow: { flexDirection: "row", gap: 10, marginTop: 12 },
  acceptButton: { flex: 1, borderRadius: 12, borderWidth: 1, borderColor: "#16A34A", paddingVertical: 12, alignItems: "center" },
  rejectButton: { flex: 1, borderRadius: 12, borderWidth: 1, borderColor: "#F97316", paddingVertical: 12, alignItems: "center" },
  acceptText: { color: "#15803D", fontWeight: "900" },
  rejectText: { color: "#EA580C", fontWeight: "900" },
  input: { minHeight: 50, borderRadius: 12, borderWidth: 1, borderColor: "#CBD5E1", paddingHorizontal: 14, marginTop: 10, color: "#0F172A", fontWeight: "700" },
  disabled: { opacity: 0.45 },
  stateContainer: { flex: 1, backgroundColor: "#FFFFFF", alignItems: "center", justifyContent: "center", padding: 24 },
  errorText: { color: "#B91C1C", fontWeight: "800", marginBottom: 12, textAlign: "center" },
});
