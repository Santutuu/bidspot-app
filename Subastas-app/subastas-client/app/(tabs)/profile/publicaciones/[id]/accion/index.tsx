import { getPublicacionMock } from "@/src/mocks/publicacionesMock";
import { AccionRequeridaMock } from "@/src/types/publicaciones";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useLocalSearchParams } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

function ActionBody({ action }: { action: AccionRequeridaMock }) {
  if (action.tipo === "ENVIAR_ITEM") {
    return (
      <>
        <Text style={styles.description}>Enviar a depósito Las Heras 2233.</Text>
        <Pressable style={styles.secondaryAction}>
          <Text style={styles.secondaryActionText}>Abrir en Maps</Text>
        </Pressable>
        <Pressable style={styles.primaryAction}>
          <Text style={styles.primaryActionText}>Ya entregué el item</Text>
        </Pressable>
      </>
    );
  }

  if (action.tipo === "COMPROBAR_ORIGEN_LICITO") {
    return (
      <>
        <Text style={styles.description}>Subí un comprobante o certificado.</Text>
        <View style={styles.uploadBox}>
          <Ionicons name="document-attach-outline" size={24} color="#2F63F6" />
          <Text style={styles.uploadText}>Añadir archivo</Text>
        </View>
      </>
    );
  }

  if (action.tipo === "PROPUESTA_COLECCION") {
    return (
      <>
        <Text style={styles.description}>
          La empresa sugiere agrupar este producto como colección.
        </Text>
        <View style={styles.actionRow}>
          <Pressable style={styles.acceptButton}>
            <Text style={styles.acceptText}>Aceptar</Text>
          </Pressable>
          <Pressable style={styles.rejectButton}>
            <Text style={styles.rejectText}>Rechazar</Text>
          </Pressable>
        </View>
      </>
    );
  }

  if (action.tipo === "ACEPTAR_POLIZA") {
    return (
      <>
        <Text style={styles.description}>
          Póliza recomendada para proteger el item durante el proceso.
        </Text>
        <View style={styles.actionRow}>
          <Pressable style={styles.acceptButton}>
            <Text style={styles.acceptText}>Aceptar</Text>
          </Pressable>
          <Pressable style={styles.rejectButton}>
            <Text style={styles.rejectText}>Rechazar</Text>
          </Pressable>
        </View>
      </>
    );
  }

  if (action.tipo === "MODIFICAR_POLIZA") {
    return (
      <>
        <Text style={styles.description}>Solicitá un aumento de cobertura.</Text>
        <TextInput
          style={styles.input}
          placeholder="Monto solicitado"
          placeholderTextColor="#94A3B8"
        />
        <Pressable style={styles.primaryAction}>
          <Text style={styles.primaryActionText}>Enviar solicitud</Text>
        </Pressable>
      </>
    );
  }

  return (
    <>
      <Text style={styles.description}>
        Revisá valor base, comisión y condiciones sugeridas.
      </Text>
      <View style={styles.actionRow}>
        <Pressable style={styles.acceptButton}>
          <Text style={styles.acceptText}>Aceptar</Text>
        </Pressable>
        <Pressable style={styles.rejectButton}>
          <Text style={styles.rejectText}>Rechazar</Text>
        </Pressable>
      </View>
    </>
  );
}

export default function AccionesRequeridasScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const publicacion = getPublicacionMock(id);
  const acciones = publicacion?.acciones ?? [];

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable onPress={() => router.back()} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.title}>Acciones requeridas</Text>
      </View>

      {acciones.length === 0 ? (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Sin acciones pendientes</Text>
          <Text style={styles.description}>
            Esta publicación no requiere respuestas por ahora.
          </Text>
        </View>
      ) : (
        acciones.map((action, index) => (
          <View key={action.id} style={styles.card}>
            <Text style={styles.cardTitle}>
              {index + 1}. {action.titulo}
            </Text>
            <ActionBody action={action} />
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F2F5FB" },
  content: {
    paddingHorizontal: 20,
    paddingTop: 22,
    paddingBottom: 40,
  },
  headerRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 22,
  },
  iconButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },
  title: {
    fontSize: 26,
    color: "#0F172A",
    fontWeight: "900",
  },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 16,
    marginBottom: 14,
  },
  cardTitle: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 8,
  },
  description: {
    fontSize: 14,
    color: "#475569",
    lineHeight: 22,
    fontWeight: "600",
    marginBottom: 12,
  },
  secondaryAction: {
    alignSelf: "flex-start",
    paddingVertical: 8,
    paddingHorizontal: 12,
  },
  secondaryActionText: {
    color: "#2F63F6",
    fontWeight: "900",
  },
  primaryAction: {
    backgroundColor: "#111827",
    borderRadius: 12,
    paddingVertical: 13,
    alignItems: "center",
  },
  primaryActionText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
  uploadBox: {
    minHeight: 92,
    borderRadius: 14,
    borderWidth: 1.5,
    borderStyle: "dashed",
    borderColor: "#93C5FD",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
  },
  uploadText: {
    color: "#2F63F6",
    fontWeight: "900",
  },
  actionRow: {
    flexDirection: "row",
    gap: 10,
  },
  acceptButton: {
    flex: 1,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#16A34A",
    paddingVertical: 12,
    alignItems: "center",
  },
  rejectButton: {
    flex: 1,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#F97316",
    paddingVertical: 12,
    alignItems: "center",
  },
  acceptText: {
    color: "#15803D",
    fontWeight: "900",
  },
  rejectText: {
    color: "#EA580C",
    fontWeight: "900",
  },
  input: {
    height: 50,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    paddingHorizontal: 14,
    marginBottom: 12,
    color: "#0F172A",
    fontWeight: "700",
  },
});
