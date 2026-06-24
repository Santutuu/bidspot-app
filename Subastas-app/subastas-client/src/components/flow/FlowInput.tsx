import { TextInput, TextInputProps, StyleSheet } from "react-native";

export default function FlowInput(props: TextInputProps) {
  return (
    <TextInput
      {...props}
      placeholderTextColor="#9CA3AF"
      style={[styles.input, props.style]}
    />
  );
}

const styles = StyleSheet.create({
  input: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    paddingHorizontal: 15,
    paddingVertical: 13,
    fontSize: 15,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: "#D1D5DB",
    color: "#111827",
    height: 54,
  },
});