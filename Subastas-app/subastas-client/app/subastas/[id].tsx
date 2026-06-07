import { useRef, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import { useLocalSearchParams } from "expo-router";
import { useDetalleSubasta } from "@/src/hooks/useDetalleSubasta";

const defaultImage = require("@/assets/images/white-old-vehicle.jpg");
const IMAGE_WIDTH = 360;

function getEstadoTexto(estado: string) {
  switch (estado) {
    case "CREADA":
      return "Subasta programada";
    case "ACTIVA":
      return "Subasta en vivo";
    case "FINALIZADA":
      return "Subasta finalizada";
    case "CANCELADA":
      return "Subasta cancelada";
    default:
      return estado;
  }
}

function getPrecioLabel(tipoPrecio: string) {
  if (tipoPrecio === "PRECIO_ACTUAL") return "Precio actual";
  if (tipoPrecio === "PRECIO_INICIAL") return "Precio inicial";
  return "Precio final";
}

function formatPrice(moneda: string, precio: number) {
  const symbol = moneda === "DOLARES" ? "USD" : "$";
  return `${symbol} ${precio}`;
}

export default function DetalleSubastaScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { detalle, loading, error, recargar } = useDetalleSubasta(id);

  const listRef = useRef<FlatList>(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  if (loading) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando detalle...</Text>
      </View>
    );
  }

  if (error || !detalle) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>{error}</Text>
        <Pressable style={styles.retryButton} onPress={recargar}>
          <Text style={styles.retryText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const baseImages =
    detalle.imagenesUrl && detalle.imagenesUrl.length > 0
      ? detalle.imagenesUrl
      : [null];

  const carouselImages = Array.from(
    { length: 3 },
    (_, index) => baseImages[index] ?? baseImages[0]
  );

  function goToImage(direction: "prev" | "next") {
    const nextIndex =
      direction === "next"
        ? Math.min(currentIndex + 1, carouselImages.length - 1)
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
          data={carouselImages}
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
            <Image
              source={item ? { uri: item } : defaultImage}
              style={styles.mainImage}
              resizeMode="cover"
            />
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
        {carouselImages.map((image, index) => (
          <Pressable
            key={index}
            onPress={() => {
              setCurrentIndex(index);
              listRef.current?.scrollToIndex({ index, animated: true });
            }}
          >
            <Image
              source={image ? { uri: image } : defaultImage}
              style={[
                styles.thumbnail,
                currentIndex === index && styles.thumbnailActive,
              ]}
              resizeMode="cover"
            />
          </Pressable>
        ))}
      </View>

      <Text style={styles.title}>{detalle.titulo}</Text>

      <Text style={styles.status}>{getEstadoTexto(detalle.estadoSubasta)}</Text>

      {detalle.estadoSubasta === "CREADA" && (
        <Text style={styles.dateText}>
          Inicia el {detalle.fechaInicio} a las {detalle.horaInicio} hs
        </Text>
      )}

      <Text style={styles.label}>{getPrecioLabel(detalle.tipoPrecio)}</Text>
      <Text style={styles.price}>
        {formatPrice(detalle.moneda, detalle.precioMostrado)}
      </Text>

      <Text style={styles.description}>{detalle.descripcion}</Text>

      <Text style={styles.auctioneer}>Martillero: {detalle.martillero}</Text>

      {detalle.puedeOfertar && (
        <>
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
        </>
      )}

      {detalle.estadoSubasta === "ACTIVA" && (
        <Pressable>
          <Text style={styles.streamingLink}>Link streaming subasta</Text>
        </Pressable>
      )}
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
    justifyContent: "center",
    gap: 20,
    marginTop: 12,
    marginBottom: 16,
  },

  thumbnail: {
    width: 100,
    height: 100,
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
    marginBottom: 8,
  },

  status: {
    fontSize: 15,
    fontWeight: "700",
    color: "#2F63F6",
    marginBottom: 6,
  },

  dateText: {
    fontSize: 14,
    color: "#555",
    marginBottom: 14,
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

  stateContainer: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },

  stateText: {
    marginTop: 10,
    fontSize: 15,
    color: "#555",
  },

  errorText: {
    fontSize: 15,
    color: "#B91C1C",
    textAlign: "center",
    marginBottom: 14,
  },

  retryButton: {
    backgroundColor: "#2F63F6",
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: 10,
  },

  retryText: {
    color: "white",
    fontWeight: "700",
  },
});