import { Categoria } from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

const categorias: { label: string; value: Categoria }[] = [
  { label: "Arte", value: "ARTE" },
  { label: "Joyas", value: "JOYAS" },
  { label: "Vehiculos", value: "VEHICULOS" },
  { label: "Ropa", value: "ROPA" },
  { label: "Otros", value: "OTROS" },
];

export default function PublicarCategoriaScreen() {
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState<{
    label: string;
    value: Categoria;
  } | null>(null);
  const [open, setOpen] = useState(false);

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.header}>
        <Text style={styles.step}>Paso 1 de 2</Text>
        <Text style={styles.title}>Elegi la categoria del item</Text>
        <Text style={styles.subtitle}>
          Esta informacion ayuda a clasificar la publicacion antes de la revision.
        </Text>
      </View>

      <View style={styles.formCard}>
        <Text style={styles.label}>Categoria</Text>

        <Pressable style={styles.selectBox} onPress={() => setOpen((value) => !value)}>
          <Text
            style={[
              styles.selectText,
              !categoriaSeleccionada && styles.placeholderText,
            ]}
          >
            {categoriaSeleccionada?.label || "Seleccionar categoria"}
          </Text>
          <Ionicons
            name={open ? "chevron-up" : "chevron-down"}
            size={22}
            color="#334155"
          />
        </Pressable>

        {open && (
          <View style={styles.dropdown}>
            {categorias.map((categoria) => (
              <Pressable
                key={categoria.value}
                style={styles.dropdownOption}
                onPress={() => {
                  setCategoriaSeleccionada(categoria);
                  setOpen(false);
                }}
              >
                <Text style={styles.dropdownText}>{categoria.label}</Text>
                {categoriaSeleccionada?.value === categoria.value && (
                  <Ionicons name="checkmark" size={20} color="#2F63F6" />
                )}
              </Pressable>
            ))}
          </View>
        )}
      </View>

      <View style={styles.actions}>
        <Pressable onPress={() => router.back()} style={styles.secondaryButton}>
          <Ionicons name="chevron-back" size={18} color="#111827" />
          <Text style={styles.secondaryButtonText}>Volver</Text>
        </Pressable>

        <Pressable
          onPress={() =>
            router.push({
              pathname: "/(tabs)/profile/publicar/detalle" as any,
              params: { categoria: categoriaSeleccionada?.value },
            })
          }
          style={[
            styles.primaryButton,
            !categoriaSeleccionada && styles.disabledButton,
          ]}
          disabled={!categoriaSeleccionada}
        >
          <Text style={styles.primaryButtonText}>Continuar</Text>
          <Ionicons name="arrow-forward" size={18} color="#FFFFFF" />
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: {
    flexGrow: 1,
    paddingHorizontal: 22,
    paddingTop: 46,
    paddingBottom: 36,
  },
  header: {
    marginBottom: 24,
  },
  step: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "900",
    marginBottom: 8,
  },
  title: {
    fontSize: 30,
    color: "#0F172A",
    fontWeight: "900",
    lineHeight: 38,
    marginBottom: 10,
  },
  subtitle: {
    fontSize: 15,
    color: "#64748B",
    lineHeight: 22,
    fontWeight: "600",
  },
  formCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
  },
  label: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 8,
  },
  selectBox: {
    minHeight: 56,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#F8FAFC",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 14,
  },
  selectText: {
    fontSize: 16,
    color: "#0F172A",
    fontWeight: "800",
  },
  placeholderText: {
    color: "#94A3B8",
  },
  dropdown: {
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    backgroundColor: "#FFFFFF",
    marginTop: 10,
    overflow: "hidden",
  },
  dropdownOption: {
    minHeight: 48,
    paddingHorizontal: 14,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderBottomWidth: 1,
    borderBottomColor: "#EEF2F8",
  },
  dropdownText: {
    fontSize: 15,
    color: "#0F172A",
    fontWeight: "700",
  },
  actions: {
    marginTop: "auto",
    flexDirection: "row",
    gap: 12,
  },
  secondaryButton: {
    flex: 1,
    height: 52,
    borderRadius: 14,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
  },
  secondaryButtonText: {
    fontSize: 15,
    color: "#111827",
    fontWeight: "900",
  },
  primaryButton: {
    flex: 1,
    height: 52,
    borderRadius: 14,
    backgroundColor: "#2F63F6",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
  },
  disabledButton: {
    opacity: 0.45,
  },
  primaryButtonText: {
    fontSize: 15,
    color: "#FFFFFF",
    fontWeight: "900",
  },
});
