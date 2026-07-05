import AuctionCard from "@/src/components/home/AuctionCard";
import { useAuth } from "@/src/context/authContext";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { useSubastasPorCategoria } from "@/src/hooks/useSubastasPorCategoria";
import { getCurrencyCode } from "@/src/utils/moneda";

import { router, useLocalSearchParams } from "expo-router";
import {
    ActivityIndicator,
    Alert,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

const categoryRank = {
  COMUN: 0,
  PLATA: 1,
  ORO: 2,
  PLATINO: 3,
};

function formatPrice(precio: number | null, moneda: string) {
  if (precio === null) {
    return "Precio no disponible";
  }

  return `${getCurrencyCode(moneda)} ${precio}`;
}

function formatCategoryName(category?: string | string[]) {
  const value = Array.isArray(category) ? category[0] : category;

  if (!value) return "Categoría";

  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

export default function CategoryScreen() {
  const { categoria } = useLocalSearchParams<{ categoria: string }>();
  const { isAuthenticated, isValidated, user } = useAuth();

  const { data, loading, error, recargar } = useSubastasPorCategoria(categoria);

  function canAccessAuction(categoriaMin: string | null) {
    if (!user?.categoria || !categoriaMin) return false;

    return (
      categoryRank[user.categoria as keyof typeof categoryRank] >=
      categoryRank[categoriaMin as keyof typeof categoryRank]
    );
  }

  function handleAuctionPress(subasta: SubastaHomeDTO) {
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
      pathname: "/subastas/[id]",
      params: { id: String(subasta.idSubasta) },
    });
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Text style={styles.title}>{formatCategoryName(categoria)}</Text>

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

      {!loading && !error && data && (
        <>
          <Text style={styles.sectionTitle}>En tiempo real</Text>

          {data.activas.length === 0 ? (
            <Text style={styles.emptyText}>
              No hay subastas en tiempo real para esta categoría.
            </Text>
          ) : (
            <View style={styles.cardsGrid}>
              {data.activas.map((subasta) => (
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

          <Text style={styles.sectionTitle}>Subastas programadas</Text>

          {data.programadas.length === 0 ? (
            <Text style={styles.emptyText}>
              No hay subastas programadas para esta categoría.
            </Text>
          ) : (
            <View style={styles.cardsGrid}>
              {data.programadas.map((subasta) => (
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
        </>
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
    paddingTop: 24,
    paddingBottom: 32,
  },

  title: {
    fontSize: 30,
    fontWeight: "800",
    color: "#111",
    marginBottom: 22,
  },

  sectionTitle: {
    fontSize: 22,
    fontWeight: "700",
    color: "#333",
    marginBottom: 16,
    marginTop: 8,
  },

  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    marginBottom: 22,
  },

  stateContainer: {
    paddingVertical: 40,
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

  emptyText: {
    fontSize: 15,
    color: "#666",
    marginBottom: 24,
  },
});
