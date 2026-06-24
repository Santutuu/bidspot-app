import { preRegisterUser } from "@/src/api/authAPI";
import { uploadDniImage } from "@/src/api/uploadAPI";
import Ionicons from "@expo/vector-icons/Ionicons";
import * as ImagePicker from "expo-image-picker";
import { router } from "expo-router";
import { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Image,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

type DocumentSide = "front" | "back";

export default function RegisterScreen() {
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);

  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [mail, setMail] = useState("");

  const [pais, setPais] = useState("");
  const [provincia, setProvincia] = useState("");
  const [ciudad, setCiudad] = useState("");
  const [cp, setCp] = useState("");
  const [direccion, setDireccion] = useState("");

  const [frontDniImage, setFrontDniImage] = useState<string | null>(null);
  const [backDniImage, setBackDniImage] = useState<string | null>(null);

  function validatePersonalData() {
    if (nombre.trim().length < 2) {
      Alert.alert("Nombre inválido", "El nombre debe tener al menos 2 caracteres.");
      return;
    }

    if (apellido.trim().length < 2) {
      Alert.alert("Apellido inválido", "El apellido debe tener al menos 2 caracteres.");
      return;
    }

    if (!isValidEmail(mail)) {
      Alert.alert("Email inválido", "Ingresá un email válido.");
      return;
    }

    setStep(2);
  }

  function validateAddress() {
    if (!pais.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu país de origen.");
      return;
    }

    if (!provincia.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu provincia.");
      return;
    }

    if (!ciudad.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu ciudad.");
      return;
    }

    if (!cp.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu código postal.");
      return;
    }

    if (!direccion.trim()) {
      Alert.alert("Campo obligatorio", "Ingresá tu dirección.");
      return;
    }

    setStep(3);
  }

  async function submitPreRegister() {
    if (!frontDniImage || !backDniImage) {
      Alert.alert("Documento incompleto", "Subí frente y dorso del DNI.");
      return;
    }

    try {
      setLoading(true);

      const frenteDNIUrl = await uploadDniImage(frontDniImage);
      const dorsoDNIUrl = await uploadDniImage(backDniImage);

      const response = await preRegisterUser({
        nombre: nombre.trim(),
        apellido: apellido.trim(),
        mail: mail.trim().toLowerCase(),
        frenteDNIUrl,
        dorsoDNIUrl,
        domicilio: {
          pais: pais.trim(),
          provincia: provincia.trim(),
          ciudad: ciudad.trim(),
          cp: cp.trim(),
          direccion: direccion.trim(),
        },
      });

      Alert.alert("Solicitud enviada", response.mensaje);

      router.replace({
        pathname: "/auth/registration-status",
        params: { mail: response.mail },
      });
    } catch (error: any) {
      const data = error.response?.data;
      const message =
        data?.message ??
        data?.error ??
        "No se pudo completar el registro.";

      Alert.alert("Error", message);
    } finally {
      setLoading(false);
    }
  }

  async function pickImage(side: DocumentSide) {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();

    if (!permission.granted) {
      Alert.alert("Permiso requerido", "Necesitamos acceso a tu galería.");
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["images"],
      allowsEditing: true,
      quality: 0.8,
    });

    if (result.canceled) return;

    const uri = result.assets[0].uri;

    if (side === "front") {
      setFrontDniImage(uri);
    } else {
      setBackDniImage(uri);
    }
  }

  async function takePhoto(side: DocumentSide) {
    const permission = await ImagePicker.requestCameraPermissionsAsync();

    if (!permission.granted) {
      Alert.alert("Permiso requerido", "Necesitamos acceso a la cámara.");
      return;
    }

    const result = await ImagePicker.launchCameraAsync({
      allowsEditing: true,
      quality: 0.8,
    });

    if (result.canceled) return;

    const uri = result.assets[0].uri;

    if (side === "front") {
      setFrontDniImage(uri);
    } else {
      setBackDniImage(uri);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.keyboardContainer}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        <Text style={styles.kicker}>Registro de postor</Text>

        <Text style={styles.title}>Solicitud inicial</Text>

        <Text style={styles.subtitle}>
          Completá tus datos. La empresa revisará tu identidad y, si te acepta,
          te asignará una categoría para participar en subastas.
        </Text>

        <View style={styles.stepper}>
          <StepDot active={step === 1} done={step > 1} label="Datos" />
          <View style={styles.stepLine} />
          <StepDot active={step === 2} done={step > 2} label="Domicilio" />
          <View style={styles.stepLine} />
          <StepDot active={step === 3} done={false} label="Documento" />
        </View>

        {step === 1 && (
          <View style={styles.panel}>
            <Text style={styles.sectionEmoji}>👤</Text>

            <Text style={styles.sectionTitle}>Datos personales</Text>

            <Text style={styles.sectionDescription}>
              En esta primera etapa no generás contraseña. Primero la empresa
              debe validar tus datos.
            </Text>

            <TextInput
              style={styles.input}
              placeholder="Nombre"
              placeholderTextColor="#9CA3AF"
              value={nombre}
              onChangeText={setNombre}
            />

            <TextInput
              style={styles.input}
              placeholder="Apellido"
              placeholderTextColor="#9CA3AF"
              value={apellido}
              onChangeText={setApellido}
            />

            <TextInput
              style={styles.input}
              placeholder="Email"
              placeholderTextColor="#9CA3AF"
              keyboardType="email-address"
              autoCapitalize="none"
              autoCorrect={false}
              value={mail}
              onChangeText={setMail}
            />

            <Pressable style={styles.button} onPress={validatePersonalData}>
              <Text style={styles.buttonText}>Continuar</Text>
            </Pressable>
          </View>
        )}

        {step === 2 && (
          <View style={styles.panel}>
            <Text style={styles.sectionEmoji}>📍</Text>

            <Text style={styles.sectionTitle}>Domicilio legal</Text>

            <Text style={styles.sectionDescription}>
              Indicá tu domicilio legal y país de origen.
            </Text>

            <TextInput
              style={styles.input}
              placeholder="País de origen"
              placeholderTextColor="#9CA3AF"
              value={pais}
              onChangeText={setPais}
            />

            <TextInput
              style={styles.input}
              placeholder="Provincia"
              placeholderTextColor="#9CA3AF"
              value={provincia}
              onChangeText={setProvincia}
            />

            <TextInput
              style={styles.input}
              placeholder="Ciudad"
              placeholderTextColor="#9CA3AF"
              value={ciudad}
              onChangeText={setCiudad}
            />

            <TextInput
              style={styles.input}
              placeholder="Código postal"
              placeholderTextColor="#9CA3AF"
              keyboardType="numeric"
              value={cp}
              onChangeText={setCp}
            />

            <TextInput
              style={styles.input}
              placeholder="Dirección"
              placeholderTextColor="#9CA3AF"
              value={direccion}
              onChangeText={setDireccion}
            />

            <View style={styles.actionsRow}>
              <Pressable style={styles.secondaryButton} onPress={() => setStep(1)}>
                <Text style={styles.secondaryButtonText}>Volver</Text>
              </Pressable>

              <Pressable style={styles.primarySmallButton} onPress={validateAddress}>
                <Text style={styles.buttonText}>Continuar</Text>
              </Pressable>
            </View>
          </View>
        )}

        {step === 3 && (
          <View style={styles.panel}>
            <Text style={styles.sectionEmoji}>🪪</Text>

            <Text style={styles.sectionTitle}>Documento de identidad</Text>

            <Text style={styles.sectionDescription}>
              Subí imágenes claras del frente y dorso del DNI.
            </Text>

            <DocumentUploadBox
              title="Frente del DNI"
              imageUri={frontDniImage}
              onPick={() => pickImage("front")}
              onCamera={() => takePhoto("front")}
            />

            <DocumentUploadBox
              title="Dorso del DNI"
              imageUri={backDniImage}
              onPick={() => pickImage("back")}
              onCamera={() => takePhoto("back")}
            />

            <View style={styles.actionsRow}>
              <Pressable style={styles.secondaryButton} onPress={() => setStep(2)}>
                <Text style={styles.secondaryButtonText}>Volver</Text>
              </Pressable>

              <Pressable
                style={[styles.primarySmallButton, loading && styles.buttonDisabled]}
                disabled={loading}
                onPress={submitPreRegister}
              >
                {loading ? (
                  <ActivityIndicator color="white" />
                ) : (
                  <Text style={styles.buttonText}>Enviar solicitud</Text>
                )}
              </Pressable>
            </View>
          </View>
        )}

        <Pressable onPress={() => router.push("/auth/login")}>
          <Text style={styles.link}>Ya tengo una clave</Text>
        </Pressable>

        <Pressable onPress={() => router.push("/auth/registration-status")}>
          <Text style={styles.secondaryLink}>Consultar estado de solicitud</Text>
        </Pressable>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

function StepDot({
  active,
  done,
  label,
}: {
  active: boolean;
  done: boolean;
  label: string;
}) {
  return (
    <View style={styles.stepItem}>
      <View style={[styles.dot, active && styles.dotActive, done && styles.dotDone]}>
        {done ? <Ionicons name="checkmark" size={14} color="white" /> : null}
      </View>

      <Text style={[styles.stepLabel, active && styles.stepLabelActive]}>
        {label}
      </Text>
    </View>
  );
}

function DocumentUploadBox({
  title,
  imageUri,
  onPick,
  onCamera,
}: {
  title: string;
  imageUri: string | null;
  onPick: () => void;
  onCamera: () => void;
}) {
  return (
    <View style={styles.uploadBox}>
      {imageUri ? (
        <Image
          source={{ uri: imageUri }}
          style={styles.previewImage}
          resizeMode="cover"
        />
      ) : (
        <View style={styles.emptyPreview}>
          <Ionicons name="document-text-outline" size={32} color="#4B5563" />
          <Text style={styles.uploadTitle}>{title}</Text>
          <Text style={styles.uploadSubtitle}>
            Imagen clara, completa y legible.
          </Text>
        </View>
      )}

      <View style={styles.uploadActions}>
        <Pressable style={styles.uploadButton} onPress={onPick}>
          <Ionicons name="image-outline" size={18} color="#1F2937" />
          <Text style={styles.uploadButtonText}>Galería</Text>
        </Pressable>

        <Pressable style={styles.uploadButton} onPress={onCamera}>
          <Ionicons name="camera-outline" size={18} color="#1F2937" />
          <Text style={styles.uploadButtonText}>Cámara</Text>
        </Pressable>
      </View>
    </View>
  );
}

function isValidEmail(value: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

const styles = StyleSheet.create({
  keyboardContainer: {
    flex: 1,
  },

  screen: {
    flex: 1,
    backgroundColor: "#F5F6FA",
  },

  container: {
    paddingHorizontal: 22,
    paddingTop: 54,
    paddingBottom: 140,
  },

  kicker: {
    color: "#2F63F6",
    fontSize: 13,
    fontWeight: "800",
    letterSpacing: 0.6,
    textTransform: "uppercase",
    marginBottom: 8,
  },

  title: {
    fontSize: 32,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 26,
  },

  stepper: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 24,
  },

  stepItem: {
    alignItems: "center",
    width: 70,
  },

  dot: {
    width: 24,
    height: 24,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: "#9CA3AF",
    backgroundColor: "#FFFFFF",
    justifyContent: "center",
    alignItems: "center",
  },

  dotActive: {
    borderColor: "#2F63F6",
    backgroundColor: "#2F63F6",
  },

  dotDone: {
    borderColor: "#2F63F6",
    backgroundColor: "#2F63F6",
  },

  stepLabel: {
    marginTop: 6,
    fontSize: 11,
    color: "#6B7280",
    fontWeight: "600",
  },

  stepLabelActive: {
    color: "#111827",
  },

  stepLine: {
    flex: 1,
    height: 1,
    backgroundColor: "#D1D5DB",
    marginBottom: 22,
  },

  panel: {
    backgroundColor: "#FFFFFF",
    borderRadius: 22,
    padding: 20,
    borderWidth: 1,
    borderColor: "#E5E7EB",
  },

  sectionEmoji: {
    fontSize: 30,
    marginBottom: 8,
  },

  sectionTitle: {
    fontSize: 22,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 6,
  },

  sectionDescription: {
    fontSize: 14,
    color: "#6B7280",
    lineHeight: 20,
    marginBottom: 18,
  },

  input: {
    backgroundColor: "#FAFAFA",
    borderRadius: 0,
    paddingHorizontal: 15,
    paddingVertical: 14,
    fontSize: 15,
    marginBottom: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    color: "#111827",
    height: 55,
  },

  button: {
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 13,
    alignItems: "center",
    marginTop: 4,
  },

  buttonDisabled: {
    opacity: 0.7,
  },

  actionsRow: {
    flexDirection: "row",
    gap: 12,
    marginTop: 4,
  },

  secondaryButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    paddingVertical: 14,
    borderRadius: 13,
    alignItems: "center",
    backgroundColor: "#FFFFFF",
  },

  primarySmallButton: {
    flex: 1,
    backgroundColor: "#2F63F6",
    paddingVertical: 14,
    borderRadius: 13,
    alignItems: "center",
  },

  secondaryButtonText: {
    color: "#1F2937",
    fontSize: 15,
    fontWeight: "700",
  },

  buttonText: {
    color: "white",
    fontSize: 15,
    fontWeight: "800",
  },

  uploadBox: {
    marginBottom: 14,
  },

  emptyPreview: {
    height: 142,
    borderRadius: 14,
    borderWidth: 1,
    borderStyle: "dashed",
    borderColor: "#CBD5E1",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#FAFAFA",
    paddingHorizontal: 16,
  },

  previewImage: {
    width: "100%",
    height: 160,
    borderRadius: 14,
    backgroundColor: "#EEE",
  },

  uploadTitle: {
    marginTop: 8,
    fontSize: 15,
    fontWeight: "800",
    color: "#111827",
  },

  uploadSubtitle: {
    marginTop: 4,
    fontSize: 13,
    color: "#6B7280",
    textAlign: "center",
  },

  uploadActions: {
    flexDirection: "row",
    gap: 10,
    marginTop: 10,
  },

  uploadButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#D1D5DB",
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    paddingVertical: 11,
    justifyContent: "center",
    alignItems: "center",
    flexDirection: "row",
    gap: 6,
  },

  uploadButtonText: {
    color: "#1F2937",
    fontWeight: "700",
    fontSize: 14,
  },

  link: {
    textAlign: "center",
    color: "#2F63F6",
    fontSize: 15,
    fontWeight: "700",
    marginTop: 22,
  },

  secondaryLink: {
    textAlign: "center",
    color: "#6B7280",
    fontSize: 14,
    fontWeight: "700",
    marginTop: 12,
  },
});