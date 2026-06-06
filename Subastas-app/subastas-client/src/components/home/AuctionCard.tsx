import { Image, Pressable, StyleSheet, Text } from "react-native";

type Props = {
  title: string;
  currentPrice: string;
  onPress?: () => void;
};

export default function AuctionCard({
  title,
  currentPrice,
  onPress,
}: Props) {
  return (
    <Pressable style={styles.card} onPress={onPress}>
      <Image
        source={require("@/assets/images/white-old-vehicle.jpg")}
        style={styles.image}
        resizeMode="cover"
      />

      <Text style={styles.title}>{title}</Text>

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