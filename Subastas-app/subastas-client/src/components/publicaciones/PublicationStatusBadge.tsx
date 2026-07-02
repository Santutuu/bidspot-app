import { EstadoPublicacion } from "@/src/types/publicaciones";
import { StyleSheet, Text, View } from "react-native";

const config: Record<
  EstadoPublicacion,
  { label: string; color: string; background: string }
> = {
  PENDIENTE: { label: "Pendiente", color: "#92400E", background: "#FEF3C7" },
  EN_REVISION: { label: "En revisión", color: "#1D4ED8", background: "#DBEAFE" },
  ACEPTADA: { label: "Aceptada", color: "#15803D", background: "#DCFCE7" },
  RECHAZADA: { label: "Rechazada", color: "#B91C1C", background: "#FEE2E2" },
  CANCELADA: { label: "Cancelada", color: "#475569", background: "#E2E8F0" },
  VENDIDA: { label: "Vendida", color: "#6D28D9", background: "#EDE9FE" },
};

type Props = {
  estado: EstadoPublicacion;
};

export default function PublicationStatusBadge({ estado }: Props) {
  const value = config[estado];

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
