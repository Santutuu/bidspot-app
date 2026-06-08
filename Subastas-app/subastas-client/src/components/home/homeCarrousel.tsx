import { FlatList, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { useRef, useState } from "react";

const slides = [
  {
    image: require("@/assets/images/moto.webp"),
    title: "Conecta con coleccionistas apasionados como tú!",
  },
  {
    image: require("@/assets/images/obras_arte.jpg"),
    title: "Ofertar nunca fue tan fácil",
  },
  {
    image: require("@/src/assets/images/camiseta-maradona.jpg"),
    title: "Descubre articulos especiales y sumalos a tu coleccion",
  },
];

const SLIDE_WIDTH = 385;

export default function HomeCarousel() {
  const listRef = useRef<FlatList>(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  function goToSlide(direction: "prev" | "next") {
    let nextIndex = currentIndex;

    if (direction === "next") {
      nextIndex = Math.min(currentIndex + 1, slides.length - 1);
    } else {
      nextIndex = Math.max(currentIndex - 1, 0);
    }

    setCurrentIndex(nextIndex);

    listRef.current?.scrollToIndex({
      index: nextIndex,
      animated: true,
    });
  }

  return (
    <View style={styles.container}>
      <FlatList
        ref={listRef}
        data={slides}
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        keyExtractor={(_, index) => index.toString()}
        onMomentumScrollEnd={(event) => {
          const index = Math.round(
            event.nativeEvent.contentOffset.x / SLIDE_WIDTH
          );
          setCurrentIndex(index);
        }}
        renderItem={({ item }) => (
          <View style={styles.slide}>
            <Image
              source={item.image}
              style={styles.image}
              resizeMode="cover"
            />

            {/* Overlay oscuro para aumentar contraste */}
            <View style={styles.overlay} />

            <View style={styles.textContainer}>
              <Text style={styles.title}>{item.title}</Text>
            </View>
          </View>
        )}
      />

      <Pressable
        style={[styles.arrowButton, styles.leftArrow]}
        onPress={() => goToSlide("prev")}
      >
        <Text style={styles.arrow}>‹</Text>
      </Pressable>

      <Pressable
        style={[styles.arrowButton, styles.rightArrow]}
        onPress={() => goToSlide("next")}
      >
        <Text style={styles.arrow}>›</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    height: 280,
    marginTop: 24,
    marginHorizontal: 10,
    borderRadius: 24,
    overflow: "hidden",
    borderWidth: 1,
    borderColor: "#DDD",
  },

  slide: {
    width: SLIDE_WIDTH,
    height: 280,
  },

  image: {
    width: SLIDE_WIDTH,
    height: 280,
  },

  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "rgba(0,0,0,0.38)",
  },

  textContainer: {
    position: "absolute",
    left: 24,
    right: 24,
    bottom: 30,
  },

  title: {
    color: "white",
    fontSize: 30,
    fontWeight: "800",
    lineHeight: 38,

    textShadowColor: "rgba(0,0,0,0.9)",
    textShadowOffset: {
      width: 0,
      height: 3,
    },
    textShadowRadius: 10,
  },

  arrowButton: {
    position: "absolute",
    top: "42%",
    width: 34,
    height: 48,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255,255,255,0.18)",
    borderRadius: 20,
  },

  leftArrow: {
    left: 10,
  },

  rightArrow: {
    right: 10,
  },

  arrow: {
    color: "white",
    fontSize: 34,
    fontWeight: "300",
  },
});