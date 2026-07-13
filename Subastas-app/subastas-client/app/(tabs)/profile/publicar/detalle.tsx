import { uploadSubastaImages } from "@/src/api/uploadAPI";
import { useTarjetas } from "@/src/hooks/useTarjetas";
import { useCrearSolicitudPublicacion } from "@/src/hooks/useSolicitudesPublicacion";
import { Categoria } from "@/src/types/solicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import * as ImagePicker from "expo-image-picker";
import { router, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Image,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    View,
} from "react-native";

export default function PublicarDetalleScreen() {
  const { categoria } = useLocalSearchParams<{ categoria?: Categoria }>();
  const [imagenes, setImagenes] = useState<string[]>([]);
  const [titulo, setTitulo] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [declaraPropiedad, setDeclaraPropiedad] = useState(false);
  const [uploading, setUploading] = useState(false);
  const { crear, loading: creando } = useCrearSolicitudPublicacion();
  const { tarjetas, cargarTarjetas } = useTarjetas();

  useEffect(() => {
    void cargarTarjetas();
  }, [cargarTarjetas]);

  function handleBack() {
    router.replace("/(tabs)/profile" as any);
  }

  async function handleAgregarFotos() {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsMultipleSelection: true,
      quality: 0.85,
      selectionLimit: Math.max(1, 6 - imagenes.length),
    });

    if (result.canceled) return;

    const nuevasImagenes = result.assets.map((asset) => asset.uri);
    setImagenes((current) => [...current, ...nuevasImagenes].slice(0, 6));
  }

  async function handlePublicar() {
    if (!categoria) {
      Alert.alert("Categoria requerida", "Volvé y seleccioná una categoría.");
      return;
    }

    if (!titulo.trim()) {
      Alert.alert("Título requerido", "Ingresá un título para el producto.");
      return;
    }

    if (!descripcion.trim()) {
      Alert.alert("Descripción requerida", "Ingresá una descripción.");
      return;
    }

    if (imagenes.length < 6) {
      Alert.alert("Fotos requeridas", "Agregá al menos 6 fotos del producto.");
      return;
    }

    if (!declaraPropiedad) {
      Alert.alert(
        "Declaración requerida",
        "Tenés que declarar que sos propietario del producto.",
      );
      return;
    }

    const tieneTarjetaPesos = tarjetas.some((tarjeta) => tarjeta.moneda === "PESOS");
    if (!tieneTarjetaPesos) {
      Alert.alert(
        "Tarjeta en pesos requerida",
        "Para solicitar una publicación necesitás tener al menos una tarjeta de crédito registrada en pesos.",
        [
          { text: "Cancelar", style: "cancel" },
          {
            text: "Ir a medios de pago",
            onPress: () => router.push("/(tabs)/financial-setup/medios-pago" as any),
          },
        ],
      );
      return;
    }

    try {
      setUploading(true);
      const imagenesUrl = await uploadSubastaImages(imagenes);

      await crear({
        categoria,
        titulo: titulo.trim(),
        descripcion: descripcion.trim(),
        imagenesUrl,
        declaracionPropiedad: true,
      });

      router.replace("/(tabs)/profile/publicaciones" as any);
    } catch (error: any) {
      Alert.alert(
        "No pudimos publicar",
        error.response?.data?.message ??
          error.response?.data?.error ??
          "Revisá los datos e intentá nuevamente.",
      );
    } finally {
      setUploading(false);
    }
  }

  const submitting = uploading || creando;

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.header}>
        <Text style={styles.step}>Paso 2 de 2</Text>
        <Text style={styles.title}>Completá los datos del producto</Text>
      </View>

      <View style={styles.formCard}>
        <View style={styles.photoHeader}>
          <View>
            <Text style={styles.label}>Fotos</Text>
            <Text style={styles.helperText}>{imagenes.length}/6 agregadas</Text>
          </View>

          <Pressable
            style={[
              styles.addPhotoButton,
              imagenes.length >= 6 && styles.disabledButton,
            ]}
            onPress={handleAgregarFotos}
            disabled={imagenes.length >= 6}
          >
            <Ionicons name="add" size={18} color="#FFFFFF" />
            <Text style={styles.addPhotoText}>Agregar</Text>
          </Pressable>
        </View>

        {imagenes.length === 0 ? (
          <Pressable style={styles.emptyPhotos} onPress={handleAgregarFotos}>
            <Ionicons name="images-outline" size={30} color="#2F63F6" />
            <Text style={styles.emptyPhotosTitle}>
              Agregá fotos del producto
            </Text>
            <Text style={styles.emptyPhotosText}>
              Podés seleccionar imágenes desde tu dispositivo.
            </Text>
          </Pressable>
        ) : (
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.photosRow}
          >
            {imagenes.map((image, index) => (
              <Image
                key={`${image}-${index}`}
                source={{ uri: image }}
                style={styles.photo}
              />
            ))}
          </ScrollView>
        )}

        <Text style={styles.label}>Título</Text>
        <TextInput
          style={styles.titleInput}
          placeholder="Ej: Reloj antiguo de bolsillo"
          placeholderTextColor="#94A3B8"
          value={titulo}
          onChangeText={setTitulo}
        />

        <Text style={styles.label}>Descripción</Text>
        <TextInput
          style={styles.descriptionInput}
          placeholder="Describí estado, procedencia, medidas y detalles relevantes."
          placeholderTextColor="#94A3B8"
          multiline
          value={descripcion}
          onChangeText={setDescripcion}
        />

        <Pressable
          style={styles.checkRow}
          onPress={() => setDeclaraPropiedad((value) => !value)}
        >
          <View
            style={[
              styles.checkbox,
              declaraPropiedad && styles.checkboxChecked,
            ]}
          >
            {declaraPropiedad && (
              <Ionicons name="checkmark" size={18} color="#FFFFFF" />
            )}
          </View>
          <Text style={styles.checkText}>
            Declaro ser el propietario del producto
          </Text>
        </Pressable>
      </View>

      <View style={styles.actions}>
        <Pressable onPress={handleBack} style={styles.secondaryButton}>
          <Ionicons name="chevron-back" size={18} color="#111827" />
          <Text style={styles.secondaryButtonText}>Volver</Text>
        </Pressable>

        <Pressable
          onPress={handlePublicar}
          style={[
            styles.primaryButton,
            (!declaraPropiedad || submitting) && styles.disabledButton,
          ]}
          disabled={!declaraPropiedad || submitting}
        >
          {submitting ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <>
              <Text style={styles.primaryButtonText}>Publicar</Text>
              <Ionicons name="checkmark" size={18} color="#FFFFFF" />
            </>
          )}
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: {
    flexGrow: 1,
    paddingHorizontal: 20,
    paddingTop: 34,
    paddingBottom: 36,
  },
  header: {
    marginBottom: 18,
  },
  step: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "900",
    marginBottom: 8,
  },
  title: {
    fontSize: 28,
    color: "#0F172A",
    fontWeight: "900",
    lineHeight: 36,
  },
  formCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 22,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
  },
  photoHeader: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 12,
  },
  label: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginBottom: 8,
  },
  helperText: {
    fontSize: 13,
    color: "#64748B",
    fontWeight: "700",
  },
  addPhotoButton: {
    minHeight: 40,
    borderRadius: 12,
    backgroundColor: "#2F63F6",
    flexDirection: "row",
    alignItems: "center",
    gap: 5,
    paddingHorizontal: 12,
  },
  addPhotoText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
  emptyPhotos: {
    minHeight: 150,
    borderRadius: 18,
    borderWidth: 1.5,
    borderStyle: "dashed",
    borderColor: "#BFDBFE",
    backgroundColor: "#F8FAFC",
    alignItems: "center",
    justifyContent: "center",
    padding: 18,
    marginBottom: 18,
  },
  emptyPhotosTitle: {
    fontSize: 16,
    color: "#0F172A",
    fontWeight: "900",
    marginTop: 10,
  },
  emptyPhotosText: {
    fontSize: 13,
    color: "#64748B",
    textAlign: "center",
    lineHeight: 19,
    marginTop: 4,
    fontWeight: "600",
  },
  photosRow: {
    gap: 10,
    paddingBottom: 18,
  },
  photo: {
    width: 96,
    height: 96,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#E5E7EB",
  },
  titleInput: {
    height: 54,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#F8FAFC",
    paddingHorizontal: 14,
    fontSize: 16,
    fontWeight: "700",
    marginBottom: 16,
    color: "#111827",
  },
  descriptionInput: {
    minHeight: 142,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#F8FAFC",
    paddingHorizontal: 14,
    paddingTop: 14,
    fontSize: 15,
    fontWeight: "600",
    marginBottom: 16,
    color: "#111827",
    textAlignVertical: "top",
    lineHeight: 21,
  },
  checkRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    paddingVertical: 4,
  },
  checkbox: {
    width: 28,
    height: 28,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: "#CBD5E1",
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
  },
  checkboxChecked: {
    backgroundColor: "#2F63F6",
    borderColor: "#2F63F6",
  },
  checkText: {
    flex: 1,
    fontSize: 14,
    color: "#334155",
    fontWeight: "800",
    lineHeight: 20,
  },
  actions: {
    marginTop: "auto",
    paddingTop: 18,
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
