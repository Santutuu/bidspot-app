import { EstadoSolicitud } from "@/src/types/solicitudesPublicacion";
import { StyleSheet, Text, View } from "react-native";

const config: Record<
  EstadoSolicitud,
  { label: string; color: string; background: string }
> = {
  PENDIENTE_REVISION: { label: "Pendiente de revisión", color: "#92400E", background: "#FEF3C7" },
  INTERES_EMPRESA: { label: "Interés de la empresa", color: "#1D4ED8", background: "#DBEAFE" },
  PENDIENTE_ENVIO: { label: "Pendiente de envío", color: "#1D4ED8", background: "#DBEAFE" },
  EN_INSPECCION: { label: "En inspección", color: "#1D4ED8", background: "#DBEAFE" },
  PENDIENTE_CONDICIONES_VENTA: { label: "Condiciones pendientes", color: "#92400E", background: "#FEF3C7" },
  PENDIENTE_POLIZA: { label: "Póliza pendiente", color: "#92400E", background: "#FEF3C7" },
  LISTA_PARA_SUBASTA: { label: "Lista para subasta", color: "#15803D", background: "#DCFCE7" },
  DEVOLUCION_PENDIENTE: { label: "Devolución pendiente", color: "#92400E", background: "#FEF3C7" },
  DEVUELTA: { label: "Devuelta", color: "#475569", background: "#E2E8F0" },
  RECHAZADA: { label: "Rechazada", color: "#B91C1C", background: "#FEE2E2" },
  CANCELADA: { label: "Cancelada", color: "#475569", background: "#E2E8F0" },
};

type Props = {
  estado: EstadoSolicitud;
};

export default function PublicationStatusBadge({ estado }: Props) {
  const value = config[estado] ?? {
    label: estado,
    color: "#475569",
    background: "#E2E8F0",
  };

  return (
    <View style={[styles.badge, { backgroundColor: value.background }]}>
      <Text style={[styles.text, { color: value.color }]}>{value.label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    alignSelf: "flex-start",
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 5,
  },
  text: {
    fontSize: 12,
    fontWeight: "900",
  },
});
