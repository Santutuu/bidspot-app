import { uploadSubastaImages } from "@/src/api/uploadAPI";
import {
  useDetalleSolicitudPublicacion,
  useResponderAccionSolicitud,
} from "@/src/hooks/useSolicitudesPublicacion";
import { AccionRequerida, ResponderAccionRequest } from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import * as ImagePicker from "expo-image-picker";
import { router, useLocalSearchParams } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

const titles: Record<AccionRequerida, string> = {
  ENVIAR_ITEM: "Enviar item",
  PROPUESTA_COLECCION: "Propuesta colección",
  ACEPTAR_CONDICIONES_VENTA: "Condiciones de venta",
  ACEPTAR_POLIZA: "Póliza de subasta",
  MODIFICAR_POLIZA: "Modificar póliza",
  COMPROBAR_ORIGEN_LICITO: "Comprobar origen lícito",
};

const condicionesVentaDemo = {
  estadoSubasta: "PROGRAMADA",
  fechaInicio: "27/07/2026 16.00hs",
  categoriaMin: "COMUN",
  moneda: "DOLARES",
  ubicacion: "Salón Central - Buenos Aires",
  linkVivo: "Sin transmisión en vivo",
  rematador: "Rematador #1",
  valorBase: 2500,
  comision: 0.1,
};

function formatUsd(value: number) {
  return `USD ${value.toLocaleString("es-AR")}`;
}

function descriptionFor(action: AccionRequerida) {
  if (action === "ENVIAR_ITEM") {
    return "Coordiná el envío del item al depósito indicado por la empresa.";
  }
  if (action === "COMPROBAR_ORIGEN_LICITO") {
    return "Adjuntá un comprobante o certificado de origen.";
  }
  if (action === "PROPUESTA_COLECCION") {
    return "La empresa sugiere agrupar este producto como colección.";
  }
  if (action === "MODIFICAR_POLIZA") {
    return "Solicitá un cambio en el monto asegurado.";
  }
  return "Revisá la propuesta y respondé si aceptás o rechazás.";
}

export default function AccionesRequeridasScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { detalle, loading, error, recargar, setDetalle } =
    useDetalleSolicitudPublicacion(id);
  const { responder, loading: responding } = useResponderAccionSolicitud();
  const [comentarios, setComentarios] = useState<Record<string, string>>({});
  const [montos, setMontos] = useState<Record<string, string>>({});
  const [archivoUrls, setArchivoUrls] = useState<Record<string, string>>({});
  const [uploadingAction, setUploadingAction] = useState<string | null>(null);

  async function submit(accion: AccionRequerida, request: ResponderAccionRequest) {
    try {
      const updated = await responder(id, accion, request);
      if (updated) {
        setDetalle(updated);
      }
      setComentarios((current) => ({ ...current, [accion]: "" }));
      setMontos((current) => ({ ...current, [accion]: "" }));
      setArchivoUrls((current) => ({ ...current, [accion]: "" }));
      await recargar();
    } catch (err: any) {
      Alert.alert(
        "No pudimos responder",
        err.response?.data?.message ??
          err.response?.data?.error ??
          "Intentá nuevamente.",
      );
    }
  }

  async function pickArchivo(accion: AccionRequerida) {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.85,
      selectionLimit: 1,
    });

    if (result.canceled || !result.assets[0]?.uri) return;

    try {
      setUploadingAction(accion);
      const [url] = await uploadSubastaImages([result.assets[0].uri]);
      setArchivoUrls((current) => ({ ...current, [accion]: url }));
    } catch {
      Alert.alert("Error", "No pudimos subir el archivo.");
    } finally {
      setUploadingAction(null);
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

  const acciones = detalle.accionesRequeridas;

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable onPress={() => router.back()} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.title}>Acciones requeridas</Text>
      </View>

      {acciones.length === 0 ? (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Sin acciones pendientes</Text>
          <Text style={styles.description}>
            Esta publicación no requiere respuestas por ahora.
          </Text>
        </View>
      ) : (
        acciones.map((accion, index) => {
          const comentario = comentarios[accion] ?? "";
          const monto = montos[accion] ?? "";
          const archivoUrl = archivoUrls[accion] ?? "";
          const busy = responding || uploadingAction === accion;

          return (
            <View key={accion} style={styles.card}>
              <Text style={styles.cardTitle}>
                {index + 1}. {titles[accion]}
              </Text>
              <Text style={styles.description}>{descriptionFor(accion)}</Text>

              {accion === "ACEPTAR_CONDICIONES_VENTA" && (
                <View style={styles.policyCard}>
                  <View style={styles.policyHeader}>
                    <View>
                      <Text style={styles.policyEyebrow}>Próxima subasta</Text>
                      <Text style={styles.policyTitle}>Condiciones de venta</Text>
                    </View>
                    <Text style={styles.policyStatus}>
                      {condicionesVentaDemo.estadoSubasta}
                    </Text>
                  </View>

                  <Text style={styles.noticeText}>
                    ¡Buenas noticias! Tu artículo fue inspeccionado y aprobado
                    para participar en la próxima subasta.
                  </Text>

                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Fecha</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.fechaInicio}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Categoría mínima</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.categoriaMin}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Moneda</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.moneda}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Lugar</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.ubicacion}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Streaming</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.linkVivo}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Rematador</Text>
                    <Text style={styles.policyValue}>
                      {condicionesVentaDemo.rematador}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Valor base</Text>
                    <Text style={styles.policyValue}>
                      {formatUsd(condicionesVentaDemo.valorBase)}
                    </Text>
                  </View>
                  <View style={styles.policyRow}>
                    <Text style={styles.policyLabel}>Comisión</Text>
                    <Text style={styles.policyValue}>
                      {(condicionesVentaDemo.comision * 100).toLocaleString("es-AR")}%
                    </Text>
                  </View>
                </View>
              )}

              {accion === "ACEPTAR_POLIZA" && (
                <View style={styles.policyCard}>
                  <View style={styles.policyHeader}>
                    <View>
                      <Text style={styles.policyEyebrow}>Propuesta de seguro</Text>
                      <Text style={styles.policyTitle}>Póliza de subasta</Text>
                    </View>
                    <Text style={styles.policyStatus}>Revisión</Text>
                  </View>

                  <Text style={styles.noticeText}>
                    Revisá la póliza contratada para tu artículo y, si querés, solicitá
                    un aumento del monto asegurado.
                  </Text>
                  <Pressable
                    style={styles.secondaryAction}
                    onPress={() =>
                      router.push({
                        pathname: "/(tabs)/profile/publicaciones/[id]/poliza" as any,
                        params: { id },
                      })
                    }
                  >
                    <Text style={styles.secondaryActionText}>Revisar póliza</Text>
                  </Pressable>
                </View>
              )}

              {accion === "ENVIAR_ITEM" && (
                <>
                  {detalle.ubicacionDeposito && (
                    <Text style={styles.description}>
                      Depósito: {detalle.ubicacionDeposito}
                    </Text>
                  )}
                  <TextInput
                    style={styles.input}
                    placeholder="Comentario"
                    placeholderTextColor="#94A3B8"
                    value={comentario}
                    onChangeText={(value) =>
                      setComentarios((current) => ({ ...current, [accion]: value }))
                    }
                  />
                  <Pressable
                    style={styles.primaryAction}
                    disabled={busy}
                    onPress={() =>
                      submit(accion, {
                        tipoRespuesta: "COMENTARIO",
                        comentario: comentario || "Coordino entrega en depósito.",
                      })
                    }
                  >
                    <Text style={styles.primaryActionText}>Enviar respuesta</Text>
                  </Pressable>
                </>
              )}

              {accion === "COMPROBAR_ORIGEN_LICITO" && (
                <>
                  <Pressable
                    style={styles.uploadBox}
                    onPress={() => pickArchivo(accion)}
                    disabled={busy}
                  >
                    <Ionicons name="document-attach-outline" size={24} color="#2F63F6" />
                    <Text style={styles.uploadText}>
                      {archivoUrl ? "Archivo cargado" : "Añadir archivo"}
                    </Text>
                  </Pressable>
                  <TextInput
                    style={styles.input}
                    placeholder="Comentario opcional"
                    placeholderTextColor="#94A3B8"
                    value={comentario}
                    onChangeText={(value) =>
                      setComentarios((current) => ({ ...current, [accion]: value }))
                    }
                  />
                  <Pressable
                    style={[styles.primaryAction, !archivoUrl && styles.disabled]}
                    disabled={busy || !archivoUrl}
                    onPress={() =>
                      submit(accion, {
                        tipoRespuesta: "ARCHIVO",
                        archivoUrl,
                        comentario,
                      })
                    }
                  >
                    <Text style={styles.primaryActionText}>Enviar comprobante</Text>
                  </Pressable>
                </>
              )}

              {accion === "MODIFICAR_POLIZA" && (
                <>
                  <TextInput
                    style={styles.input}
                    placeholder="Monto asegurado solicitado"
                    placeholderTextColor="#94A3B8"
                    keyboardType="numeric"
                    value={monto}
                    onChangeText={(value) =>
                      setMontos((current) => ({ ...current, [accion]: value }))
                    }
                  />
                  <Pressable
                    style={[
                      styles.primaryAction,
                      (!monto || Number.isNaN(Number(monto))) && styles.disabled,
                    ]}
                    disabled={busy || !monto || Number.isNaN(Number(monto))}
                    onPress={() =>
                      submit(accion, {
                        tipoRespuesta: "MONTO_ASEGURADO",
                        montoAseguradoSolicitado: Number(monto),
                        comentario: comentario || undefined,
                      })
                    }
                  >
                    <Text style={styles.primaryActionText}>Enviar solicitud</Text>
                  </Pressable>
                </>
              )}

              {accion !== "ENVIAR_ITEM" &&
                accion !== "COMPROBAR_ORIGEN_LICITO" &&
                accion !== "MODIFICAR_POLIZA" && (
                  <View style={styles.actionRow}>
                    <Pressable
                      style={styles.acceptButton}
                      disabled={busy}
                      onPress={() =>
                        submit(accion, {
                          tipoRespuesta: "ACEPTACION",
                          aceptada: true,
                        })
                      }
                    >
                      <Text style={styles.acceptText}>Aceptar</Text>
                    </Pressable>
                    <Pressable
                      style={styles.rejectButton}
                      disabled={busy}
                      onPress={() =>
                        submit(accion, {
                          tipoRespuesta: "RECHAZO",
                          aceptada: false,
                          comentario: "No acepto la propuesta.",
                        })
                      }
                    >
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
    marginBottom: 22,
  },
  iconButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },
  title: {
    fontSize: 26,
    color: "#0F172A",
    fontWeight: "900",
  },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 16,
    marginBottom: 14,
  },
  cardTitle: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 8,
  },
  description: {
    fontSize: 14,
    color: "#475569",
    lineHeight: 22,
    fontWeight: "600",
    marginBottom: 12,
  },
  policyCard: {
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#D8E1EE",
    backgroundColor: "#F8FAFC",
    padding: 14,
    marginBottom: 14,
  },
  policyHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: 12,
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
    paddingBottom: 12,
    marginBottom: 10,
  },
  policyEyebrow: {
    fontSize: 11,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    marginBottom: 3,
  },
  policyTitle: {
    fontSize: 16,
    color: "#0F172A",
    fontWeight: "900",
  },
  policyStatus: {
    color: "#92400E",
    backgroundColor: "#FEF3C7",
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 5,
    fontSize: 11,
    fontWeight: "900",
  },
  noticeText: {
    color: "#334155",
    fontSize: 13,
    fontWeight: "700",
    lineHeight: 19,
    marginBottom: 10,
  },
  policyRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
    paddingVertical: 6,
  },
  policyLabel: {
    flex: 1,
    color: "#64748B",
    fontSize: 13,
    fontWeight: "800",
  },
  policyValue: {
    flex: 1,
    color: "#0F172A",
    fontSize: 13,
    fontWeight: "900",
    textAlign: "right",
  },
  primaryAction: {
    backgroundColor: "#111827",
    borderRadius: 12,
    paddingVertical: 13,
    alignItems: "center",
  },
  primaryActionText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
  secondaryAction: {
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#111827",
    paddingVertical: 12,
    alignItems: "center",
    marginTop: 12,
  },
  secondaryActionText: {
    color: "#111827",
    fontWeight: "900",
  },
  uploadBox: {
    minHeight: 92,
    borderRadius: 14,
    borderWidth: 1.5,
    borderStyle: "dashed",
    borderColor: "#93C5FD",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    marginBottom: 12,
  },
  uploadText: {
    color: "#2F63F6",
    fontWeight: "900",
  },
  actionRow: {
    flexDirection: "row",
    gap: 10,
  },
  acceptButton: {
    flex: 1,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#16A34A",
    paddingVertical: 12,
    alignItems: "center",
  },
  rejectButton: {
    flex: 1,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#F97316",
    paddingVertical: 12,
    alignItems: "center",
  },
  acceptText: {
    color: "#15803D",
    fontWeight: "900",
  },
  rejectText: {
    color: "#EA580C",
    fontWeight: "900",
  },
  input: {
    minHeight: 50,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    paddingHorizontal: 14,
    marginBottom: 12,
    color: "#0F172A",
    fontWeight: "700",
  },
  disabled: {
    opacity: 0.45,
  },
  stateContainer: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
  },
  errorText: {
    color: "#B91C1C",
    fontWeight: "800",
    marginBottom: 12,
    textAlign: "center",
  },
});
