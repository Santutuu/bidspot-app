import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  onCancel: () => void;
  onConfirm: () => void;
  loading?: boolean;
  cancelText?: string;
  confirmText?: string;
};

export default function FlowActionRow({
  onCancel,
  onConfirm,
  loading = false,
  cancelText = "Cancelar",
  confirmText = "Guardar",
}: Props) {
  return (
    <View style={styles.row}>
      <Pressable style={styles.cancelButton} onPress={onCancel} disabled={loading}>
        <Text style={styles.cancelText}>{cancelText}</Text>
      </Pressable>

      <Pressable
        style={[styles.confirmButton, loading && styles.disabled]}
        onPress={onConfirm}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="white" />
        ) : (
          <Text style={styles.confirmText}>{confirmText}</Text>
        )}
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    gap: 14,
    marginTop: 10,
    alignItems: "center",
  },

  cancelButton: {
    flex: 1,
    height: 54,
    borderRadius: 16,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    justifyContent: "center",
    alignItems: "center",
  },

  confirmButton: {
    flex: 1,
    height: 54,
    borderRadius: 16,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
  },

  cancelText: {
    color: "#111827",
    fontSize: 15,
    fontWeight: "800",
  },

  confirmText: {
    color: "white",
    fontSize: 15,
    fontWeight: "900",
  },

  disabled: {
    opacity: 0.7,
  },
});