import HomeCarousel from "@/src/components/home/homeCarrousel";
import CategoryCircle from "@/src/components/home/CategoryCircle";
import AuctionCard from "@/src/components/home/AuctionCard";

import { router } from "expo-router";
import { ScrollView, StyleSheet, Text, View } from "react-native";

const categories = ["Arte", "Joyas", "Autos", "Muebles"];

const auctions = [
  {
    id: "1",
    title: "TÍTULO",
    currentPrice: "Precio actual",
  },
  {
    id: "2",
    title: "TÍTULO",
    currentPrice: "Precio actual",
  },
  {
    id: "3",
    title: "TÍTULO",
    currentPrice: "Precio actual",
  },
  {
    id: "4",
    title: "TÍTULO",
    currentPrice: "Precio actual",
  },
];

export default function HomeScreen() {
  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <HomeCarousel />

      <Text style={styles.sectionTitle}>Categorías</Text>

      <View style={styles.categoriesRow}>
        {categories.map((category) => (
          <CategoryCircle
            key={category}
            name={category}
            onPress={() =>
              router.push(`/subastas/category/${category.toLowerCase()}`)
            }
          />
        ))}
      </View>

      <View style={styles.cardsGrid}>
        {auctions.map((auction) => (
          <AuctionCard
            key={auction.id}
            title={auction.title}
            currentPrice={auction.currentPrice}
            onPress={() => router.push(`/subastas/${auction.id}`)}
          />
        ))}
      </View>
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

  sectionTitle: {
    marginTop: 25,
    marginBottom: 20,
    textAlign: "center",
    fontSize: 28,
    fontWeight: "600",
    color: "#333",
  },

  categoriesRow: {
    flexDirection: "row",
    justifyContent: "space-around",
    paddingHorizontal: 14,
    marginBottom: 22,
  },

  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    paddingHorizontal: 18,
  },
});