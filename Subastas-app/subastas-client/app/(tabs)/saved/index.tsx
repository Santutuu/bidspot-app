import AuctionCard from "@/src/components/home/AuctionCard";
import { useAuth } from "@/src/context/authContext";
import { useSubastasGuardadas } from "@/src/context/subastasGuardadasContext";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { getCurrencyCode } from "@/src/utils/moneda";
import { router, useFocusEffect } from "expo-router";
import { useCallback } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";

function formatPrice(precio: number | null, moneda: string) {
  if (precio === null) return "Precio no disponible";

  return `${getCurrencyCode(moneda)} ${precio}`;
}

export default function SavedScreen() {
  const { isAuthenticated, isValidated } = useAuth();
  const {
    subastasGuardadas,
    loading,
    error,
    recargar,
    eliminar,
    estaGuardada,
    pendingIds,
  } = useSubastasGuardadas();

  useFocusEffect(
    useCallback(() => {
      if (isAuthenticated && isValidated) {
        void recargar();
      }
    }, [isAuthenticated, isValidated, recargar]),
  );

  function handleAuctionPress(subasta: SubastaHomeDTO) {
    router.push({
      pathname: "/(tabs)/subastas/[id]" as any,
      params: { id: String(subasta.idSubasta) },
    });
  }

  if (!isAuthenticated || !isValidated) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.stateTitle}>Iniciá sesión</Text>
        <Text style={styles.stateText}>
          Necesitás una cuenta validada para ver tus subastas guardadas.
        </Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Text style={styles.title}>Subastas guardadas</Text>

      {loading ? (
        <View style={styles.stateContainer}>
          <ActivityIndicator size="large" color="#2F63F6" />
        </View>
      ) : error ? (
        <View style={styles.stateContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={() => recargar()}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : subastasGuardadas.length === 0 ? (
        <View style={styles.stateContainer}>
          <Text style={styles.stateTitle}>
            No tenés subastas guardadas todavía.
          </Text>
          <Text style={styles.stateText}>
            Explorá subastas y tocá el marcador para encontrarlas acá.
          </Text>
        </View>
      ) : (
        <View style={styles.cardsGrid}>
          {subastasGuardadas.map((subasta) => (
            <AuctionCard
              key={subasta.idSubasta}
              title={subasta.titulo}
              currentPrice={formatPrice(subasta.precio, subasta.moneda)}
              estadoSubasta={subasta.estadoSubasta}
              fechaInicio={subasta.fechaInicio}
              categoriaMin={subasta.categoriaMin}
              imageUrl={subasta.imagenUrl}
              isSaved={estaGuardada(subasta.idSubasta)}
              savedLoading={pendingIds.has(subasta.idSubasta)}
              onToggleSaved={() => {
                void eliminar(subasta.idSubasta);
              }}
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

  content: {
    paddingTop: 26,
    paddingBottom: 28,
  },

  title: {
    marginBottom: 24,
    textAlign: "center",
    fontSize: 26,
    fontWeight: "700",
    color: "#111827",
  },

  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    paddingHorizontal: 18,
  },

  stateContainer: {
    paddingHorizontal: 24,
    paddingVertical: 34,
    alignItems: "center",
  },

  stateTitle: {
    fontSize: 17,
    fontWeight: "800",
    color: "#111827",
    textAlign: "center",
    marginBottom: 8,
  },

  stateText: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
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
