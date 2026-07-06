import AuctionCard from "@/src/components/home/AuctionCard";
import { getSubastasPorCategoria } from "@/src/api/subastaAPI";
import { useAuth } from "@/src/context/authContext";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { getCurrencyCode } from "@/src/utils/moneda";
import { router } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

const categorias = ["ARTE", "JOYAS", "VEHICULOS", "ROPA", "OTROS"] as const;

const categoryRank = {
  COMUN: 0,
  PLATA: 1,
  ORO: 2,
  PLATINO: 3,
};

function normalizeText(value: string) {
  return value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function formatPrice(precio: number | null, moneda: string) {
  if (precio === null) return "Precio no disponible";

  return `${getCurrencyCode(moneda)} ${precio}`;
}

export default function SearchScreen() {
  const { pendingRegistrationMail, isAuthenticated, isValidated, user } =
    useAuth();
  const [busqueda, setBusqueda] = useState("");
  const [subastas, setSubastas] = useState<SubastaHomeDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function cargarSubastas() {
    try {
      setLoading(true);
      setError(null);

      const responses = await Promise.all(
        categorias.map((categoria) => getSubastasPorCategoria(categoria)),
      );

      const merged = responses.flatMap((response) => [
        ...response.activas,
        ...response.programadas,
      ]);

      const deduplicatedMap = new Map<number, SubastaHomeDTO>();

      merged.forEach((subasta) => {
        deduplicatedMap.set(subasta.idSubasta, subasta);
      });

      setSubastas(Array.from(deduplicatedMap.values()));
    } catch (err) {
      console.error("Error cargando subastas para búsqueda:", err);
      setError("No pudimos cargar subastas para buscar.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void cargarSubastas();
  }, []);

  const query = normalizeText(busqueda);

  const resultados = useMemo(() => {
    if (!query) return subastas;

    return subastas.filter((subasta) => {
      const titulo = normalizeText(subasta.titulo);
      const categoria = normalizeText(subasta.categoriaMin ?? "");

      return titulo.includes(query) || categoria.includes(query);
    });
  }, [query, subastas]);

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

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <Text style={styles.title}>Buscar subastas</Text>

      <TextInput
        style={styles.input}
        placeholder="Escribí título o categoría"
        placeholderTextColor="#6B7280"
        value={busqueda}
        onChangeText={setBusqueda}
        autoCapitalize="none"
        autoCorrect={false}
      />

      {loading ? (
        <View style={styles.stateContainer}>
          <ActivityIndicator size="large" color="#2F63F6" />
          <Text style={styles.stateText}>Cargando subastas...</Text>
        </View>
      ) : error ? (
        <View style={styles.stateContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable style={styles.retryButton} onPress={cargarSubastas}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        </View>
      ) : resultados.length === 0 ? (
        <View style={styles.stateContainer}>
          <Text style={styles.stateText}>
            No encontramos subastas con esa búsqueda.
          </Text>
        </View>
      ) : (
        <View style={styles.cardsGrid}>
          {resultados.map((subasta) => (
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
    marginBottom: 16,
  },
  input: {
    height: 50,
    borderWidth: 1.5,
    borderColor: "#D1D5DB",
    borderRadius: 12,
    paddingHorizontal: 14,
    fontSize: 16,
    color: "#111827",
    backgroundColor: "#F9FAFB",
    marginBottom: 20,
  },
  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
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
});