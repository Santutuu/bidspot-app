import AuctionCard from "@/src/components/home/AuctionCard";
import CategoryCircle from "@/src/components/home/CategoryCircle";
import HomeCarousel from "@/src/components/home/homeCarrousel";
import { useAuth } from "@/src/context/authContext";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { useSubastasRecomendadas } from "@/src/hooks/useSubastasRecomendadas";
import { router } from "expo-router";
import { useEffect, useState } from "react";
import {
    ActivityIndicator,
    Alert,
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

const categoryRank = {
  COMUN: 0,
  PLATA: 1,
  ORO: 2,
  PLATINO: 3,
};

function formatPrice(precio: number | null, moneda: string) {
  if (precio === null) return "Precio no disponible";

  const currencyCode = moneda === "DOLARES" || moneda === "USD" ? "USD" : "ARS";
  return `${currencyCode} ${precio}`;
}

export default function HomeScreen() {
  const { pendingRegistrationMail, isAuthenticated, isValidated, user } =
    useAuth();
  const { subastas, loading, error, recargar } = useSubastasRecomendadas();
  const [showSplash, setShowSplash] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setShowSplash(false), 1600);
    return () => clearTimeout(timer);
  }, []);

  function canAccessAuction(categoriaMin: string | null) {
    if (!user?.categoria || !categoriaMin) return false;

    return (
      categoryRank[user.categoria as keyof typeof categoryRank] >=
      categoryRank[categoriaMin as keyof typeof categoryRank]
    );
  }

  function handleAuctionPress(subasta: SubastaHomeDTO) {
    if (pendingRegistrationMail && !isAuthenticated) {
      router.push({
        pathname: "/(tabs)/auth/registration-status" as any,
        params: { mail: pendingRegistrationMail },
      });
      return;
    }

    if (!isAuthenticated || !isValidated) {
      router.push("/auth/login");
      return;
    }

    if (!canAccessAuction(subasta.categoriaMin)) {
      Alert.alert(
        "Categoria insuficiente",
        "Tu categoria de usuario no permite acceder a esta subasta.",
      );
      return;
    }

    router.push({
      pathname: "/(tabs)/subastas/[id]" as any,
      params: { id: String(subasta.idSubasta) },
    });
  }

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
                pathname: "/(tabs)/subastas/category/[categoria]" as any,
                params: { categoria: category.value },
              })
            }
          />
        ))}
      </ScrollView>

      <Text style={styles.sectionTitle}>Subastas recomendadas</Text>

      {loading ? (
        <View style={styles.stateContainer}>
          <ActivityIndicator size="large" color="#2F63F6" />
        </View>
      ) : error ? (
        <View style={styles.stateContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={recargar}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : subastas.length === 0 ? (
        <View style={styles.stateContainer}>
          <Text style={styles.stateText}>No hay subastas disponibles.</Text>
        </View>
      ) : (
        <View style={styles.cardsGrid}>
          {subastas.map((subasta) => (
            <AuctionCard
              key={subasta.idSubasta}
              title={subasta.titulo}
              currentPrice={formatPrice(subasta.precio, subasta.moneda)}
              showPrice={isAuthenticated}
              estadoSubasta={subasta.estadoSubasta}
              fechaInicio={subasta.fechaInicio}
              categoriaMin={subasta.categoriaMin}
              imageUrl={subasta.imagenUrl}
              onPress={() => handleAuctionPress(subasta)}
            />
          ))}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#FFFFFF" },
  content: { paddingBottom: 24 },
  splashContainer: { flex: 1, backgroundColor: "#0F172A" },
  splashImage: { ...StyleSheet.absoluteFillObject },
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
