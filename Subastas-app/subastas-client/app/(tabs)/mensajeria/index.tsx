import Ionicons from "@expo/vector-icons/Ionicons";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

export default function MensajeriaScreen() {
  return (
    <View style={styles.screen}>
      <View style={styles.headerCard}>
        <View style={styles.avatarWrap}>
          <Ionicons name="business" size={18} color="#1D4ED8" />
        </View>

        <View style={styles.headerMeta}>
          <Text style={styles.headerTitle}>Administrador Bidmax</Text>
          <Text style={styles.headerSubtitle}>Mensajes de la empresa</Text>
        </View>

        <Pressable style={styles.callButton}>
          <Ionicons name="mail-unread-outline" size={18} color="#1D4ED8" />
        </Pressable>
      </View>

      <ScrollView
        style={styles.chatArea}
        contentContainerStyle={styles.chatContent}
        showsVerticalScrollIndicator={false}
      >
        <Text style={styles.dayDivider}>Hoy</Text>

        <View style={styles.messageRowSupport}>
          <View style={styles.messageMetaWrap}>
            <View style={styles.inlineAvatar}>
              <Ionicons name="person-outline" size={15} color="#1E293B" />
            </View>
          </View>
          <View style={styles.bubbleSupport}>
            <Text style={styles.messageTextSupport}>
              Felicitaciones Santino, el item "nombre" es suyo.{"\n"}
              Los costos del item a continuacion
            </Text>
          </View>
        </View>

        <View style={styles.messageRowSupport}>
          <View style={styles.messageMetaWrap}>
            <View style={styles.inlineAvatar}>
              <Ionicons name="person-outline" size={15} color="#1E293B" />
            </View>
          </View>
          <View style={styles.bubbleSupportStrong}>
            <Text style={styles.priceLine}>Precio item: $9999</Text>
            <Text style={styles.priceLine}>Comision: $9999</Text>
            <Text style={styles.actionLine}>COMPLETAR COMPRA</Text>
          </View>
        </View>
      </ScrollView>

      <View style={styles.footerInfo}>
        <Ionicons name="information-circle-outline" size={16} color="#64748B" />
        <Text style={styles.footerText}>
          Canal informativo. No admite respuestas.
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#F1F5F9",
  },
  headerCard: {
    marginHorizontal: 16,
    marginTop: 14,
    marginBottom: 10,
    backgroundColor: "#FFFFFF",
    borderRadius: 16,
    paddingHorizontal: 14,
    paddingVertical: 12,
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    shadowColor: "#0F172A",
    shadowOpacity: 0.08,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 3,
  },
  avatarWrap: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "#DBEAFE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  headerMeta: {
    flex: 1,
  },
  headerTitle: {
    color: "#0F172A",
    fontSize: 15,
    fontWeight: "800",
  },
  headerSubtitle: {
    marginTop: 2,
    color: "#64748B",
    fontSize: 12,
    fontWeight: "600",
  },
  callButton: {
    width: 34,
    height: 34,
    borderRadius: 17,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#EFF6FF",
  },
  chatArea: {
    flex: 1,
  },
  chatContent: {
    paddingHorizontal: 16,
    paddingBottom: 12,
  },
  dayDivider: {
    alignSelf: "center",
    color: "#475569",
    fontSize: 12,
    fontWeight: "700",
    marginBottom: 12,
    paddingVertical: 6,
    paddingHorizontal: 12,
    backgroundColor: "#E2E8F0",
    borderRadius: 999,
  },
  messageMetaWrap: {
    width: 34,
    alignItems: "center",
    marginRight: 8,
  },
  inlineAvatar: {
    width: 30,
    height: 30,
    borderRadius: 15,
    borderWidth: 1.5,
    borderColor: "#94A3B8",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFFFFF",
  },
  messageRowSupport: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 10,
    width: "100%",
  },
  bubbleSupport: {
    maxWidth: "86%",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 16,
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  bubbleSupportStrong: {
    maxWidth: "86%",
    backgroundColor: "#F8FAFC",
    borderWidth: 2,
    borderColor: "#A8B2C0",
    borderRadius: 16,
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  messageTextSupport: {
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600",
    color: "#0F172A",
  },
  priceLine: {
    fontSize: 14,
    lineHeight: 20,
    color: "#0F172A",
    fontWeight: "700",
  },
  actionLine: {
    marginTop: 4,
    fontSize: 17,
    fontWeight: "900",
    letterSpacing: 0.1,
    color: "#2563EB",
  },
  footerInfo: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingHorizontal: 18,
    paddingVertical: 12,
    borderTopWidth: 1,
    borderTopColor: "#E2E8F0",
    backgroundColor: "#FFFFFF",
  },
  footerText: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "600",
  },
});
