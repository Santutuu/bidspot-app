import AuctionCard from "@/src/components/home/AuctionCard";
import CategoryCircle from "@/src/components/home/CategoryCircle";
import HomeCarousel from "@/src/components/home/homeCarrousel";
import { useSubastasRecomendadas } from "@/src/hooks/useSubastasRecomendadas";

import { router } from "expo-router";
import { useEffect, useState } from "react";
import {
    ActivityIndicator,
    Image,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

const categories = [
  { label: "Arte", value: "ARTE" },
  { label: "Joyas", value: "JOYAS" },
  { label: "Vehículos", value: "VEHICULOS" },
  { label: "Ropa", value: "ROPA" },
  { label: "Otros", value: "OTROS" },
];

function formatPrice(
  precioActual: number | null,
  precioVisible: boolean,
  moneda: string,
) {
  if (!precioVisible || precioActual === null) {
    return "Precio no disponible";
  }

  const symbol = moneda === "DOLARES" ? "USD" : "$";

  return `${symbol} ${precioActual}`;
}

export default function HomeScreen() {
  const { subastas, loading, error, recargar } = useSubastasRecomendadas();
  const [showSplash, setShowSplash] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setShowSplash(false), 3000);
    return () => clearTimeout(timer);
  }, []);

  if (showSplash) {
    return (
      <View style={styles.splashContainer}>
        <Image
          source={require("@/src/assets/images/banner-logo.png")}
          style={styles.splashImage}
          resizeMode="cover"
        />
        <View style={styles.splashOverlay} />
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <HomeCarousel />

      <Text style={styles.sectionTitle}>Categorías</Text>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.categoriesScrollContent}
      >
        {categories.map((category) => (
          <CategoryCircle
            key={category.value}
            name={category.label}
            onPress={() =>
              router.push({
                pathname: "/subastas/category/[categoria]",
                params: { categoria: category.value },
              })
            }
          />
        ))}
      </ScrollView>

      <Text style={styles.sectionTitle}>Subastas recomendadas</Text>

      {loading && (
        <View style={styles.stateContainer}>
          <ActivityIndicator size="large" color="#2F63F6" />
          <Text style={styles.stateText}>Cargando subastas...</Text>
        </View>
      )}

      {!loading && error && (
        <View style={styles.stateContainer}>
          <Text style={styles.errorText}>{error}</Text>

          <Pressable style={styles.retryButton} onPress={recargar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      )}

      {!loading && !error && subastas.length === 0 && (
        <View style={styles.stateContainer}>
          <Text style={styles.stateText}>
            No hay subastas recomendadas disponibles.
          </Text>
        </View>
      )}

      {!loading && !error && subastas.length > 0 && (
        <View style={styles.cardsGrid}>
          {subastas.map((subasta) => (
            <AuctionCard
              key={subasta.id}
              title={subasta.titulo}
              currentPrice={formatPrice(
                subasta.precioActual,
                subasta.precioVisible,
                subasta.moneda,
              )}
              imageUrl={subasta.imagenUrl}
              onPress={() =>
                router.push({
                  pathname: "/subastas/[id]",
                  params: { id: String(subasta.id) },
                })
              }
            />
          ))}
        </View>
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
    paddingBottom: 24,
  },

  splashContainer: {
    flex: 1,
    backgroundColor: "#0F172A",
  },

  splashImage: {
    ...StyleSheet.absoluteFillObject,
    width: "100%",
    height: "100%",
  },

  splashOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "rgba(15, 23, 42, 0.35)",
  },

  sectionTitle: {
    marginTop: 25,
    marginBottom: 20,
    textAlign: "center",
    fontSize: 28,
    fontWeight: "600",
    color: "#333",
  },

  categoriesScrollContent: {
    paddingHorizontal: 15,
    paddingBottom: 4,
    gap: 5,
  },

  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    paddingHorizontal: 18,
  },

  stateContainer: {
    paddingHorizontal: 24,
    paddingVertical: 26,
    alignItems: "center",
  },

  stateText: {
    marginTop: 10,
    fontSize: 15,
    color: "#555",
    textAlign: "center",
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
    fontSize: 15,
    fontWeight: "700",
  },
});
