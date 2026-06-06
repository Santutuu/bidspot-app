import { Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  name: string;
  onPress?: () => void;
};

export default function CategoryCircle({ name, onPress }: Props) {
  return (
    <Pressable style={styles.container} onPress={onPress}>
      <View style={styles.circle} />
      <Text style={styles.label}>{name}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
  
    width: 88,
    
  },

  circle: {
    width: 80,
    height: 80,
    borderRadius: 38,
    borderWidth: 1.5,
    borderColor: "#111",
    backgroundColor: "#FFFFFF",
    
  },

  label: {
    marginTop: 7,
    fontSize: 18,
    textAlign: "center",
    color: "#222",
  },

  
});