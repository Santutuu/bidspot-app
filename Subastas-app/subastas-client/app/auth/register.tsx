import { registerUser } from "@/src/api/authAPI";
import { useAuth } from "@/src/context/authContext";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router } from "expo-router";
import { useState } from "react";
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

type DocumentSide = "front" | "back";

export default function RegisterScreen() {
  const { login } = useAuth();

  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);

  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [mail, setMail] = useState("");
  const [password, setPassword] = useState("");

  const [provincia, setProvincia] = useState("");
  const [ciudad, setCiudad] = useState("");
  const [cp, setCp] = useState("");
  const [direccion, setDireccion] = useState("");

  const [frontDniImage, setFrontDniImage] = useState<string | null>(null);
  const [backDniImage, setBackDniImage] = useState<string | null>(null);

  function validatePersonalData() {
    if (!nombre.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu nombre.");
    if (!apellido.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu apellido.");
    if (!mail.includes("@")) return Alert.alert("Email inválido", "Ingresá un email válido.");
    if (password.length < 6) return Alert.alert("Contraseña inválida", "Debe tener al menos 6 caracteres.");

    setStep(2);
  }

  function validateAddress() {
    if (!provincia.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu provincia.");
    if (!ciudad.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu ciudad.");
    if (!cp.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu código postal.");
    if (!direccion.trim()) return Alert.alert("Campo obligatorio", "Ingresá tu dirección.");

    setStep(3);
  }

  async function submitRegister() {
    try {
      setLoading(true);

      const response = await registerUser({
        nombre: nombre.trim(),
        apellido: apellido.trim(),
        mail: mail.trim(),
        password,
        frenteDNIUrl: frontDniImage ?? "dni-frente-pendiente.jpg",
        dorsoDNIUrl: backDniImage ?? "dni-dorso-pendiente.jpg",
        domicilio: {
          provincia: provincia.trim(),
          ciudad: ciudad.trim(),
          cp: cp.trim(),
          direccion: direccion.trim(),
        },
      });

      await login(response);

      Alert.alert("Solicitud enviada", "Tu cuenta quedó pendiente de validación.");
      router.replace("/(tabs)/home");
    } catch (error) {
      console.error("Error register:", error);
      Alert.alert("Error", "No se pudo completar el registro.");
    } finally {
      setLoading(false);
    }
  }

  async function pickImage(side: DocumentSide) {
    Alert.alert("Funcionalidad pendiente", "La selección de imágenes se conectará después.");
  }

  async function takePhoto(side: DocumentSide) {
    Alert.alert("Funcionalidad pendiente", "La cámara se conectará después.");
  }

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.kicker}>Registro de postor</Text>
      <Text style={styles.title}>Creá tu cuenta</Text>

      <Text style={styles.subtitle}>
        Completá la solicitud en tres pasos. La empresa validará tus datos antes
        de habilitarte para participar en subastas.
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
            Usaremos estos datos para identificar tu cuenta.
          </Text>

          <TextInput style={styles.input} placeholder="Nombre" placeholderTextColor="#9CA3AF" value={nombre} onChangeText={setNombre} />
          <TextInput style={styles.input} placeholder="Apellido" placeholderTextColor="#9CA3AF" value={apellido} onChangeText={setApellido} />
          <TextInput style={styles.input} placeholder="Email" placeholderTextColor="#9CA3AF" keyboardType="email-address" autoCapitalize="none" value={mail} onChangeText={setMail} />
          <TextInput style={styles.input} placeholder="Contraseña" placeholderTextColor="#9CA3AF" secureTextEntry value={password} onChangeText={setPassword} />

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
            Indicá el domicilio asociado a tu documentación.
          </Text>

          <TextInput style={styles.input} placeholder="Provincia" placeholderTextColor="#9CA3AF" value={provincia} onChangeText={setProvincia} />
          <TextInput style={styles.input} placeholder="Ciudad" placeholderTextColor="#9CA3AF" value={ciudad} onChangeText={setCiudad} />
          <TextInput style={styles.input} placeholder="Código postal" placeholderTextColor="#9CA3AF" keyboardType="numeric" value={cp} onChangeText={setCp} />
          <TextInput style={styles.input} placeholder="Dirección" placeholderTextColor="#9CA3AF" value={direccion} onChangeText={setDireccion} />

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

          <DocumentUploadBox title="Frente del DNI" imageUri={frontDniImage} onPick={() => pickImage("front")} onCamera={() => takePhoto("front")} />
          <DocumentUploadBox title="Dorso del DNI" imageUri={backDniImage} onPick={() => pickImage("back")} onCamera={() => takePhoto("back")} />

          <View style={styles.actionsRow}>
            <Pressable style={styles.secondaryButton} onPress={() => setStep(2)}>
              <Text style={styles.secondaryButtonText}>Volver</Text>
            </Pressable>

            <Pressable
              style={[styles.primarySmallButton, loading && styles.buttonDisabled]}
              disabled={loading}
              onPress={submitRegister}
            >
              {loading ? <ActivityIndicator color="white" /> : <Text style={styles.buttonText}>Enviar</Text>}
            </Pressable>
          </View>
        </View>
      )}

      <Pressable onPress={() => router.push("/auth/login")}>
        <Text style={styles.link}>Ya tengo una cuenta</Text>
      </Pressable>
    </ScrollView>
  );
}

function StepDot({ active, done, label }: { active: boolean; done: boolean; label: string }) {
  return (
    <View style={styles.stepItem}>
      <View style={[styles.dot, active && styles.dotActive, done && styles.dotDone]}>
        {done ? <Ionicons name="checkmark" size={14} color="white" /> : null}
      </View>
      <Text style={[styles.stepLabel, active && styles.stepLabelActive]}>{label}</Text>
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
        <Image source={{ uri: imageUri }} style={styles.previewImage} resizeMode="cover" />
      ) : (
        <View style={styles.emptyPreview}>
          <Ionicons name="document-text-outline" size={32} color="#4B5563" />
          <Text style={styles.uploadTitle}>{title}</Text>
          <Text style={styles.uploadSubtitle}>Imagen clara, completa y legible.</Text>
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

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F5F6FA" },
  container: { paddingHorizontal: 22, paddingTop: 54, paddingBottom: 42 },
  kicker: { color: "#2F63F6", fontSize: 13, fontWeight: "800", letterSpacing: 0.6, textTransform: "uppercase", marginBottom: 8 },
  title: { fontSize: 32, fontWeight: "800", color: "#111827", marginBottom: 10 },
  subtitle: { fontSize: 15, color: "#6B7280", lineHeight: 22, marginBottom: 26 },
  stepper: { flexDirection: "row", alignItems: "center", marginBottom: 24 },
  stepItem: { alignItems: "center", width: 70 },
  dot: { width: 24, height: 24, borderRadius: 12, borderWidth: 1.5, borderColor: "#9CA3AF", backgroundColor: "#FFFFFF", justifyContent: "center", alignItems: "center" },
  dotActive: { borderColor: "#2F63F6", backgroundColor: "#EAF0FF" },
  dotDone: { borderColor: "#2F63F6", backgroundColor: "#2F63F6" },
  stepLabel: { marginTop: 6, fontSize: 11, color: "#6B7280", fontWeight: "600" },
  stepLabelActive: { color: "#111827" },
  stepLine: { flex: 1, height: 1, backgroundColor: "#D1D5DB", marginBottom: 22 },
  panel: { backgroundColor: "#FFFFFF", borderRadius: 22, padding: 20, borderWidth: 1, borderColor: "#E5E7EB" },
  sectionEmoji: { fontSize: 30, marginBottom: 8 },
  sectionTitle: { fontSize: 22, fontWeight: "800", color: "#111827", marginBottom: 6 },
  sectionDescription: { fontSize: 14, color: "#6B7280", lineHeight: 20, marginBottom: 18 },
  input: { backgroundColor: "#FAFAFA", borderRadius: 0, paddingHorizontal: 15, paddingVertical: 14, fontSize: 15, marginBottom: 28, borderWidth: 1, borderColor: "#E5E7EB", color: "#111827", height: 55 },
  button: { backgroundColor: "#2F63F6", paddingVertical: 15, borderRadius: 13, alignItems: "center", marginTop: 4 },
  buttonDisabled: { opacity: 0.7 },
  actionsRow: { flexDirection: "row", gap: 12, marginTop: 4 },
  secondaryButton: { flex: 1, borderWidth: 1, borderColor: "#CBD5E1", paddingVertical: 14, borderRadius: 13, alignItems: "center", backgroundColor: "#FFFFFF" },
  primarySmallButton: { flex: 1, backgroundColor: "#2F63F6", paddingVertical: 14, borderRadius: 13, alignItems: "center" },
  secondaryButtonText: { color: "#1F2937", fontSize: 15, fontWeight: "700" },
  buttonText: { color: "white", fontSize: 15, fontWeight: "800" },
  uploadBox: { marginBottom: 14 },
  emptyPreview: { height: 142, borderRadius: 14, borderWidth: 1, borderStyle: "dashed", borderColor: "#CBD5E1", justifyContent: "center", alignItems: "center", backgroundColor: "#FAFAFA", paddingHorizontal: 16 },
  previewImage: { width: "100%", height: 160, borderRadius: 14, backgroundColor: "#EEE" },
  uploadTitle: { marginTop: 8, fontSize: 15, fontWeight: "800", color: "#111827" },
  uploadSubtitle: { marginTop: 4, fontSize: 13, color: "#6B7280", textAlign: "center" },
  uploadActions: { flexDirection: "row", gap: 10, marginTop: 10 },
  uploadButton: { flex: 1, borderWidth: 1, borderColor: "#D1D5DB", backgroundColor: "#FFFFFF", borderRadius: 12, paddingVertical: 11, justifyContent: "center", alignItems: "center", flexDirection: "row", gap: 6 },
  uploadButtonText: { color: "#1F2937", fontWeight: "700", fontSize: 14 },
  link: { textAlign: "center", color: "#2F63F6", fontSize: 15, fontWeight: "700", marginTop: 22 },
});