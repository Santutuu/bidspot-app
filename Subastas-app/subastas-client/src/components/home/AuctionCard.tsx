import { Image, Pressable, StyleSheet, Text } from "react-native";

type Props = {
  title: string;
  currentPrice: string;
  imageUrl?: string | null;
  onPress?: () => void;
};

const defaultImage = require("@/assets/images/white-old-vehicle.jpg");

export default function AuctionCard({
  title,
  currentPrice,
  imageUrl,
  onPress,
}: Props) {
  const imageSource =
    imageUrl && imageUrl.trim().length > 0
      ? { uri: imageUrl }
      : defaultImage;

  return (
    <Pressable style={styles.card} onPress={onPress}>
      <Image source={imageSource} style={styles.image} resizeMode="cover" />

      <Text style={styles.title} numberOfLines={2}>
        {title}
      </Text>

      <Text style={styles.price}>{currentPrice}</Text>
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
    marginBottom: 12,
    backgroundColor: "#EEE",
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
});