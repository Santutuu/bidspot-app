import Ionicons from "@expo/vector-icons/Ionicons";
import { ActivityIndicator, Image, Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  title: string;
  currentPrice?: string;
  showPrice?: boolean;
  estadoSubasta?: "PROGRAMADA" | "ACTIVA" | "FINALIZADA" | "CANCELADA";
  fechaInicio?: string | null;
  categoriaMin?: string | null;
  imageUrl?: string | null;
  isSaved?: boolean;
  savedLoading?: boolean;
  onToggleSaved?: () => void;
  onPress?: () => void;
};

const defaultImage = require("@/assets/images/white-old-vehicle.jpg");

function formatAuctionStatus(
  estadoSubasta?: Props["estadoSubasta"],
  fechaInicio?: string | null
) {
  if (estadoSubasta === "PROGRAMADA") {
    if (!fechaInicio) return "Programada";

    const fecha = new Date(fechaInicio);
    if (Number.isNaN(fecha.getTime())) return fechaInicio;

    return `Inicia ${fecha.toLocaleDateString("es-AR")}`;
  }

  if (estadoSubasta === "ACTIVA") return "En vivo";

  return null;
}

function formatCategory(categoriaMin?: string | null) {
  if (!categoriaMin) return null;

  return categoriaMin.charAt(0).toUpperCase() + categoriaMin.slice(1).toLowerCase();
}

export default function AuctionCard({
  title,
  currentPrice,
  showPrice = true,
  estadoSubasta,
  fechaInicio,
  categoriaMin,
  imageUrl,
  isSaved,
  savedLoading = false,
  onToggleSaved,
  onPress,
}: Props) {
  const imageSource =
    imageUrl && imageUrl.trim().length > 0
      ? { uri: imageUrl }
      : defaultImage;
  const statusText = formatAuctionStatus(estadoSubasta, fechaInicio);
  const categoryText = formatCategory(categoriaMin);

  return (
    <Pressable style={styles.card} onPress={onPress}>
      <View style={styles.imageWrap}>
        <Image source={imageSource} style={styles.image} resizeMode="cover" />

        {onToggleSaved ? (
          <Pressable
            style={styles.savedButton}
            onPress={onToggleSaved}
            disabled={savedLoading}
          >
            {savedLoading ? (
              <ActivityIndicator size="small" color="#111827" />
            ) : (
              <Ionicons
                name={isSaved ? "bookmark" : "bookmark-outline"}
                size={22}
                color={isSaved ? "#111827" : "#111827"}
              />
            )}
          </Pressable>
        ) : null}
      </View>

      <Text style={styles.title} numberOfLines={2}>
        {title}
      </Text>

      {statusText ? <Text style={styles.status}>{statusText}</Text> : null}

      {showPrice && currentPrice ? (
        <Text style={styles.price}>{currentPrice}</Text>
      ) : null}

      {categoryText ? (
        <Text style={styles.categoryBadge}>Categoria {categoryText}</Text>
      ) : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: "48%",
    backgroundColor: "#FFFFFF",
    borderWidth: 1.5,
    borderColor: "#111",
    borderRadius: 8,
    padding: 5,
    marginBottom: 18,
  },

  image: {
    width: "100%",
    height: 135,
    borderRadius: 8,
    backgroundColor: "#EEE",
  },

  imageWrap: {
    marginBottom: 12,
    position: "relative",
  },

  savedButton: {
    position: "absolute",
    top: 8,
    right: 8,
    width: 34,
    height: 34,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 17,
    backgroundColor: "rgba(255,255,255,0.92)",
  },

  title: {
    fontSize: 17,
    fontWeight: "700",
    color: "#111",
  },

  price: {
    marginTop: 5,
    fontSize: 14,
    color: "#555",
  },

  status: {
    marginTop: 6,
    fontSize: 13,
    fontWeight: "700",
    color: "#2F63F6",
  },

  categoryBadge: {
    alignSelf: "flex-start",
    marginTop: 8,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 8,
    backgroundColor: "#EEF2FF",
    color: "#3730A3",
    fontSize: 12,
    fontWeight: "700",
  },
});
