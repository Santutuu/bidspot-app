import PublicationStatusBadge from "@/src/components/publicaciones/PublicationStatusBadge";
import { EstadoSolicitud } from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  publicacion: {
    id?: string;
    idSolicitud?: number;
    titulo: string;
    categoria: string;
    estado: EstadoSolicitud;
    imagenes?: number[];
    imagenUrl?: string | null;
    precioInicial?: number;
    precioFinal?: number;
  };
  onPress: () => void;
};

const defaultImage = require("@/assets/images/white-old-vehicle.jpg");

export default function PublicationCard({ publicacion, onPress }: Props) {
  const imageSource = publicacion.imagenUrl
    ? { uri: publicacion.imagenUrl }
    : publicacion.imagenes?.[0] ?? defaultImage;

  return (
    <Pressable style={styles.card} onPress={onPress}>
      <Image
        source={imageSource}
        style={styles.image}
        resizeMode="cover"
      />

      <View style={styles.content}>
        <View style={styles.topRow}>
          <Text style={styles.title} numberOfLines={2}>
            {publicacion.titulo}
          </Text>
          <Ionicons name="chevron-forward" size={18} color="#94A3B8" />
        </View>

        <Text style={styles.category}>{publicacion.categoria}</Text>

        <View style={styles.footer}>
          <PublicationStatusBadge estado={publicacion.estado} />
          {(publicacion.precioInicial || publicacion.precioFinal) && (
            <Text style={styles.price}>
              ARS {publicacion.precioFinal ?? publicacion.precioInicial}
            </Text>
          )}
        </View>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    height: 128,
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    flexDirection: "row",
    padding: 12,
    marginBottom: 14,
    shadowColor: "#0F172A",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  image: {
    width: 104,
    height: 104,
    borderRadius: 16,
    backgroundColor: "#E5E7EB",
  },
  content: {
    flex: 1,
    paddingLeft: 13,
    justifyContent: "space-between",
  },
  topRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 8,
  },
  title: {
    flex: 1,
    fontSize: 16,
    lineHeight: 21,
    fontWeight: "900",
    color: "#0F172A",
  },
  category: {
    fontSize: 13,
    color: "#64748B",
    fontWeight: "800",
  },
  footer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 8,
  },
  price: {
    fontSize: 12,
    color: "#0F172A",
    fontWeight: "900",
  },
});
