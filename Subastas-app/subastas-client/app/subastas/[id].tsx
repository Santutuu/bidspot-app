import { useRef, useState } from "react";
import {
  FlatList,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

const images = [
  require("@/assets/images/white-old-vehicle.jpg"),
  require("@/assets/images/white-old-vehicle.jpg"),
  require("@/assets/images/white-old-vehicle.jpg"),
];

const IMAGE_WIDTH = 360;

export default function DetalleSubastaScreen() {
  const listRef = useRef<FlatList>(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  function goToImage(direction: "prev" | "next") {
    const nextIndex =
      direction === "next"
        ? Math.min(currentIndex + 1, images.length - 1)
        : Math.max(currentIndex - 1, 0);

    setCurrentIndex(nextIndex);

    listRef.current?.scrollToIndex({
      index: nextIndex,
      animated: true,
    });
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.carouselContainer}>
        <FlatList
          ref={listRef}
          data={images}
          horizontal
          pagingEnabled
          showsHorizontalScrollIndicator={false}
          keyExtractor={(_, index) => index.toString()}
          onMomentumScrollEnd={(event) => {
            const index = Math.round(
              event.nativeEvent.contentOffset.x / IMAGE_WIDTH
            );
            setCurrentIndex(index);
          }}
          renderItem={({ item }) => (
            <Image source={item} style={styles.mainImage} resizeMode="cover" />
          )}
        />

        <Pressable
          style={[styles.arrowButton, styles.leftArrow]}
          onPress={() => goToImage("prev")}
        >
          <Text style={styles.arrow}>‹</Text>
        </Pressable>

        <Pressable
          style={[styles.arrowButton, styles.rightArrow]}
          onPress={() => goToImage("next")}
        >
          <Text style={styles.arrow}>›</Text>
        </Pressable>
      </View>

      <View style={styles.thumbnailRow}>
        {images.map((image, index) => (
          <Pressable
            key={index}
            onPress={() => {
              setCurrentIndex(index);
              listRef.current?.scrollToIndex({ index, animated: true });
            }}
          >
            <Image
              source={image}
              style={[
                styles.thumbnail,
                currentIndex === index && styles.thumbnailActive,
              ]}
              resizeMode="cover"
            />
          </Pressable>
        ))}
      </View>

      <Text style={styles.title}>Rayo McQueen modelo 97</Text>

      <Text style={styles.label}>Precio actual</Text>
      <Text style={styles.price}>USD 150.50</Text>

      <Text style={styles.description}>
        Vehículo de colección en excelente estado. Pieza única con gran valor
        histórico. Ideal para coleccionistas y amantes de los autos clásicos.
      </Text>

      <Text style={styles.auctioneer}>Martillero: Angel Rodriguez</Text>

      <Text style={styles.bidLabel}>Monto a ofertar</Text>
      <TextInput
        style={styles.input}
        placeholder="Ingresá tu oferta"
        placeholderTextColor="#888"
        keyboardType="numeric"
      />

      <Pressable style={styles.bidButton}>
        <Text style={styles.bidButtonText}>Ofertar</Text>
      </Pressable>

      <Pressable>
        <Text style={styles.streamingLink}>Link streaming subasta</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },

  content: {
    paddingHorizontal: 18,
    paddingTop: 18,
    paddingBottom: 40,
  },

  carouselContainer: {
    width: IMAGE_WIDTH,
    height: 360,
    alignSelf: "center",
    borderRadius: 28,
    overflow: "hidden",
    borderWidth: 1.4,
    borderColor: "#222",
    backgroundColor: "#F3F3F3",
    position: "relative",
  },

  mainImage: {
    width: IMAGE_WIDTH,
    height: 360,
  },

  arrowButton: {
    position: "absolute",
    top: "43%",
    width: 34,
    height: 46,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255,255,255,0.7)",
    borderRadius: 18,
  },

  leftArrow: {
    left: 8,
  },

  rightArrow: {
    right: 8,
  },

  arrow: {
    fontSize: 36,
    color: "#222",
    fontWeight: "300",
  },

  thumbnailRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 10,
    marginTop: 12,
    marginBottom: 16,
  },

  thumbnail: {
    width: 105,
    height: 75,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#999",
    backgroundColor: "#EEE",
  },

  thumbnailActive: {
    borderWidth: 2,
    borderColor: "#2F63F6",
  },

  title: {
    fontSize: 23,
    fontWeight: "700",
    color: "#111",
    marginBottom: 16,
  },

  label: {
    fontSize: 15,
    color: "#333",
    marginBottom: 4,
  },

  price: {
    fontSize: 34,
    fontWeight: "700",
    color: "#111",
    marginBottom: 16,
  },

  description: {
    fontSize: 16,
    color: "#333",
    lineHeight: 23,
    marginBottom: 22,
  },

  auctioneer: {
    fontSize: 14,
    color: "#555",
    fontWeight: "600",
    marginBottom: 28,
  },

  bidLabel: {
    fontSize: 16,
    color: "#222",
    fontWeight: "600",
    marginBottom: 8,
  },

  input: {
    borderBottomWidth: 1.5,
    borderBottomColor: "#444",
    paddingVertical: 8,
    fontSize: 17,
    marginBottom: 24,
  },

  bidButton: {
    borderWidth: 1.5,
    borderColor: "#16A34A",
    paddingVertical: 14,
    alignItems: "center",
    marginBottom: 16,
  },

  bidButtonText: {
    color: "#111",
    fontSize: 18,
    fontWeight: "700",
  },

  streamingLink: {
    color: "#2563EB",
    fontSize: 14,
    fontWeight: "600",
    textAlign: "center",
  },
});