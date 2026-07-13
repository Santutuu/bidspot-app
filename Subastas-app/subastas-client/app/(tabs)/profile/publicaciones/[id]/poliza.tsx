import {
    useAumentarPolizaSolicitud,
    usePolizaSolicitud,
} from "@/src/hooks/useSolicitudesPublicacion";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useLocalSearchParams } from "expo-router";
import { useMemo, useState } from "react";
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

function formatMoney(value: number) {
  return `ARS ${value.toLocaleString("es-AR", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  })}`;
}

function formatRate(value: number) {
  return `${(value * 100).toLocaleString("es-AR", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  })}%`;
}

function parseAmount(value: string) {
  const normalized = value.replace(/\./g, "").replace(",", ".");
  const amount = Number(normalized);
  return Number.isFinite(amount) ? amount : NaN;
}

export default function PolizaSolicitudScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const idSolicitud = Number(id);
  const validIdSolicitud = Number.isFinite(idSolicitud) && idSolicitud > 0;
  const { poliza, loading, error, recargar, setPoliza } = usePolizaSolicitud(
    validIdSolicitud ? String(idSolicitud) : undefined,
  );
  const { aumentar, loading: submitting } = useAumentarPolizaSolicitud();
  const [nuevoMonto, setNuevoMonto] = useState("");
  const [validation, setValidation] = useState<string | null>(null);

  const amount = useMemo(() => parseAmount(nuevoMonto), [nuevoMonto]);
  const preview = useMemo(() => {
    if (!poliza || Number.isNaN(amount)) return null;
    const nuevoPremio = amount * poliza.tasaSeguro;
    return {
      nuevoPremio,
      diferencia: nuevoPremio - poliza.premio,
    };
  }, [amount, poliza]);

  function validate() {
    if (!poliza) return "No pudimos cargar la póliza.";
    if (!nuevoMonto.trim()) return "Ingresá el nuevo monto asegurado.";
    if (Number.isNaN(amount)) return "El monto debe ser numérico.";
    if (amount <= 0) return "El monto debe ser mayor a cero.";
    if (amount <= poliza.montoAsegurado) {
      return "El nuevo monto debe ser mayor al valor asegurado actual.";
    }
    return null;
  }

  async function submit() {
    const message = validate();
    if (message) {
      setValidation(message);
      return;
    }

    try {
      setValidation(null);
      const updated = await aumentar(idSolicitud, amount);
      if (updated) {
        setPoliza(updated);
        setNuevoMonto("");
        Alert.alert(
          "Solicitud enviada",
          "El aumento de póliza quedó solicitado.",
        );
      }
    } catch (err: any) {
      Alert.alert(
        "No pudimos solicitar el aumento",
        err.response?.data?.message ??
          err.response?.data?.error ??
          "Intentá nuevamente.",
      );
    }
  }

  if (!validIdSolicitud) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>
          No encontramos una solicitud válida.
        </Text>
        <Pressable
          style={styles.retryButton}
          onPress={() => router.replace("/(tabs)/profile" as any)}
        >
          <Text style={styles.retryText}>Volver</Text>
        </Pressable>
      </View>
    );
  }

  if (loading && !poliza) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator color="#2F63F6" />
        <Text style={styles.stateText}>Cargando póliza...</Text>
      </View>
    );
  }

  if (error || !poliza) {
    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>
          {error ?? "No encontramos esta póliza."}
        </Text>
        <Pressable style={styles.retryButton} onPress={recargar}>
          <Text style={styles.retryText}>Reintentar</Text>
        </Pressable>
      </View>
    );
  }

  const validationPreview = nuevoMonto.trim() ? validate() : null;
  const submitDisabled =
    submitting || !!validationPreview || !nuevoMonto.trim();

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.headerRow}>
        <Pressable
          onPress={() => router.replace("/(tabs)/profile" as any)}
          style={styles.iconButton}
        >
          <Ionicons name="chevron-back" size={30} color="#111827" />
        </Pressable>
        <Text style={styles.headerTitle}>Póliza de seguro</Text>
      </View>

      <View style={styles.card}>
        <View style={styles.policyHeader}>
          <View>
            <Text style={styles.eyebrow}>Artículo</Text>
            <Text style={styles.title}>{poliza.tituloItem}</Text>
          </View>
          <Text style={styles.status}>{poliza.estado}</Text>
        </View>

        <View style={styles.row}>
          <Text style={styles.label}>Compañía</Text>
          <Text style={styles.value}>{poliza.compania}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Nro. póliza</Text>
          <Text style={styles.value}>{poliza.nroPoliza}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Valor asegurado actual</Text>
          <Text style={styles.value}>{formatMoney(poliza.montoAsegurado)}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Premio actual</Text>
          <Text style={styles.value}>{formatMoney(poliza.premio)}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Precio base</Text>
          <Text style={styles.value}>{formatMoney(poliza.precioBase)}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Tasa</Text>
          <Text style={styles.value}>{formatRate(poliza.tasaSeguro)}</Text>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Aumentar valor asegurado</Text>
        {poliza.estado === "AUMENTO_SOLICITADO" && (
          <Text style={styles.successText}>Solicitud de aumento enviada.</Text>
        )}

        <TextInput
          style={styles.input}
          placeholder="Nuevo monto asegurado"
          placeholderTextColor="#94A3B8"
          keyboardType="numeric"
          value={nuevoMonto}
          onChangeText={(value) => {
            setNuevoMonto(value);
            setValidation(null);
          }}
        />

        {(validation || validationPreview) && (
          <Text style={styles.errorText}>
            {validation ?? validationPreview}
          </Text>
        )}

        {preview && amount > poliza.montoAsegurado && (
          <View style={styles.previewBox}>
            <View style={styles.row}>
              <Text style={styles.label}>Nuevo valor asegurado</Text>
              <Text style={styles.value}>{formatMoney(amount)}</Text>
            </View>
            <View style={styles.row}>
              <Text style={styles.label}>Nuevo premio estimado</Text>
              <Text style={styles.value}>
                {formatMoney(preview.nuevoPremio)}
              </Text>
            </View>
            <View style={styles.row}>
              <Text style={styles.label}>Diferencia a pagar</Text>
              <Text style={styles.value}>
                {formatMoney(preview.diferencia)}
              </Text>
            </View>
          </View>
        )}

        <Pressable
          style={[styles.primaryAction, submitDisabled && styles.disabled]}
          disabled={submitDisabled}
          onPress={submit}
        >
          <Text style={styles.primaryActionText}>
            {submitting ? "Enviando..." : "Solicitar aumento"}
          </Text>
        </Pressable>
      </View>
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
    marginBottom: 20,
  },
  iconButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "900",
    color: "#0F172A",
  },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 16,
    marginBottom: 14,
  },
  policyHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: 12,
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
    paddingBottom: 12,
    marginBottom: 10,
  },
  eyebrow: {
    fontSize: 11,
    color: "#64748B",
    fontWeight: "900",
    textTransform: "uppercase",
    marginBottom: 4,
  },
  title: {
    fontSize: 20,
    color: "#0F172A",
    fontWeight: "900",
    lineHeight: 26,
    maxWidth: 210,
  },
  status: {
    color: "#92400E",
    backgroundColor: "#FEF3C7",
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 5,
    fontSize: 11,
    fontWeight: "900",
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
    paddingVertical: 7,
  },
  label: {
    flex: 1,
    color: "#64748B",
    fontSize: 13,
    fontWeight: "800",
  },
  value: {
    flex: 1,
    color: "#0F172A",
    fontSize: 13,
    fontWeight: "900",
    textAlign: "right",
  },
  sectionTitle: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "900",
    marginBottom: 12,
  },
  successText: {
    color: "#15803D",
    backgroundColor: "#DCFCE7",
    borderRadius: 12,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontWeight: "900",
    marginBottom: 12,
  },
  input: {
    minHeight: 50,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    paddingHorizontal: 14,
    marginBottom: 10,
    color: "#0F172A",
    fontWeight: "700",
  },
  previewBox: {
    borderRadius: 14,
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    paddingHorizontal: 12,
    marginBottom: 12,
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
  disabled: {
    opacity: 0.45,
  },
  stateContainer: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
  },
  stateText: {
    color: "#64748B",
    fontWeight: "800",
    marginTop: 10,
  },
  errorText: {
    color: "#B91C1C",
    fontWeight: "800",
    marginBottom: 12,
    textAlign: "center",
  },
  retryButton: {
    backgroundColor: "#111827",
    borderRadius: 12,
    paddingHorizontal: 18,
    paddingVertical: 12,
  },
  retryText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
});
