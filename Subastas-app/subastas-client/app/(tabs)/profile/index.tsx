import { getCurrentUser, getRegistrationStatus } from "@/src/api/authAPI";
import { obtenerCuentaCobro, obtenerTarjetas } from "@/src/api/meAPI";
import { useAuth } from "@/src/context/authContext";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useFocusEffect } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    View,
} from "react-native";

type BadgeConfig = {
  label: string;
  medalIcon: "medal-outline" | "trophy-outline" | "diamond-outline";
  accentColor: string;
  backgroundColor: string;
};

function getBadgeConfig(categoria: string | null | undefined): BadgeConfig {
  const normalized = (categoria ?? "PLATA").toUpperCase();

  if (normalized === "COMUN") {
    return {
      label: "Común",
      medalIcon: "medal-outline",
      accentColor: "#8A5A2B",
      backgroundColor: "#F8E8DA",
    };
  }

  if (normalized === "ORO") {
    return {
      label: "Oro",
      medalIcon: "trophy-outline",
      accentColor: "#B87A00",
      backgroundColor: "#FFF3D8",
    };
  }

  if (normalized === "PLATINO") {
    return {
      label: "Platino",
      medalIcon: "diamond-outline",
      accentColor: "#2D4C66",
      backgroundColor: "#E7F4FF",
    };
  }

  return {
    label: "Plata",
    medalIcon: "medal-outline",
    accentColor: "#516A85",
    backgroundColor: "#E8EEF5",
  };
}

function formatCardPreview(numero: string | null | undefined) {
  if (!numero) return "••••";

  const digits = numero.replace(/\D/g, "");
  const last4 = digits.slice(-4);
  return last4 ? `•••• ${last4}` : numero;
}

function formatCbuPreview(cbu: string | null | undefined) {
  if (!cbu) return "No disponible";

  const digits = cbu.replace(/\D/g, "");
  const last4 = digits.slice(-4);
  return last4 ? `Finaliza en ${last4}` : "No disponible";
}

export default function ProfileScreen() {
  const {
    user,
    loadingAuth,
    isAuthenticated,
    isValidated,
    requiresPaymentSetup,
    pendingRegistrationMail,
    logout,
    refreshUser,
  } = useAuth();

  const [checkingStatus, setCheckingStatus] = useState(false);
  const [financialLoading, setFinancialLoading] = useState(false);
  const [tarjetaPreview, setTarjetaPreview] = useState<string>("••••");
  const [cuentaPreview, setCuentaPreview] = useState<string>("No disponible");
  const refreshUserRef = useRef(refreshUser);

  useEffect(() => {
    refreshUserRef.current = refreshUser;
  }, [refreshUser]);

  useFocusEffect(
    useCallback(() => {
      if (loadingAuth) return;

      let active = true;

      async function evaluateProfileFlow() {
        try {
          setCheckingStatus(true);

          if (isAuthenticated) {
            const freshUser = await getCurrentUser();
            await refreshUserRef.current();

            if (!active) return;

            if (
              freshUser.estado === "VALIDADO" &&
              freshUser.claveGenerada &&
              freshUser.requiereMedioDePago
            ) {
              router.replace("/(tabs)/financial-setup" as any);
              return;
            }

            if (freshUser.estado === "VALIDADO" && !freshUser.claveGenerada) {
              router.replace({
                pathname: "/(tabs)/auth/complete-registration" as any,
                params: { mail: freshUser.mail },
              });
              return;
            }

            return;
          }

          if (!pendingRegistrationMail) return;

          const response = await getRegistrationStatus(pendingRegistrationMail);

          if (!active) return;

          if (response.estado === "VALIDADO" && response.puedeGenerarClave) {
            router.replace({
              pathname: "/(tabs)/auth/complete-registration" as any,
              params: { mail: response.mail },
            });
          }
        } catch {
          // En error de red mantenemos la UI actual para no romper la navegación.
        } finally {
          if (active) {
            setCheckingStatus(false);
          }
        }
      }

      void evaluateProfileFlow();

      return () => {
        active = false;
      };
    }, [loadingAuth, isAuthenticated, pendingRegistrationMail]),
  );

  useFocusEffect(
    useCallback(() => {
      if (
        loadingAuth ||
        !isAuthenticated ||
        !isValidated ||
        requiresPaymentSetup
      ) {
        return;
      }

      let active = true;

      async function loadFinancialSummary() {
        try {
          setFinancialLoading(true);

          const [tarjetas, cuenta] = await Promise.all([
            obtenerTarjetas(),
            obtenerCuentaCobro(),
          ]);

          if (!active) return;

          setTarjetaPreview(
            formatCardPreview(
              tarjetas[0]?.numeroEnmascarado ?? tarjetas[0]?.numero,
            ),
          );
          setCuentaPreview(formatCbuPreview(cuenta?.cbu));
        } catch {
          if (!active) return;
          setTarjetaPreview("••••");
          setCuentaPreview("No disponible");
        } finally {
          if (active) {
            setFinancialLoading(false);
          }
        }
      }

      void loadFinancialSummary();

      return () => {
        active = false;
      };
    }, [loadingAuth, isAuthenticated, isValidated, requiresPaymentSetup]),
  );

  async function handleLogout() {
    await logout();
    router.replace("/(tabs)/profile");
  }

  function showComingSoon(label: string) {
    Alert.alert(
      "Próximamente",
      `${label} estará disponible en una próxima versión.`,
    );
  }

  if (loadingAuth || checkingStatus) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#FFFFFF" />
        <Text style={styles.pendingText}>Cargando...</Text>
      </View>
    );
  }

  if (!isAuthenticated && pendingRegistrationMail) {
    return (
      <View style={styles.pendingContainer}>
        <Text style={styles.pendingTitle}>
          Tu cuenta se encuentra en revisión
        </Text>

        <Text style={styles.pendingSubtitle}>
          En breve nos pondremos en contacto. Cuando la empresa valide tu
          cuenta, vas a poder generar tu clave personal.
        </Text>

        <Text style={styles.mailText}>{pendingRegistrationMail}</Text>

        <Text style={styles.hourglass}>⌛</Text>

        <Pressable
          style={styles.transparentButton}
          onPress={() => router.replace("/(tabs)/home")}
        >
          <Text style={styles.transparentButtonText}>Volver al inicio</Text>
        </Pressable>
      </View>
    );
  }

  if (!isAuthenticated || !user) {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Tu perfil</Text>

        <Text style={styles.subtitle}>
          Iniciá sesión o registrate para participar en subastas.
        </Text>

        <Pressable
          style={styles.primaryButton}
          onPress={() => router.push("/auth/login")}
        >
          <Text style={styles.primaryButtonText}>Iniciar sesión</Text>
        </Pressable>

        <Pressable
          style={styles.secondaryButton}
          onPress={() => router.push("/auth/register")}
        >
          <Text style={styles.secondaryButtonText}>Crear cuenta</Text>
        </Pressable>
      </View>
    );
  }

  if (isValidated && requiresPaymentSetup) {
    const badge = getBadgeConfig(user.categoria);

    return (
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.container}
      >
        <Text style={styles.title}>Hola {user.nombre}</Text>

        <Text style={styles.subtitle}>
          Agregá configuración financiera para completar tu registro.
        </Text>

        <Pressable
          style={[
            styles.medalBadge,
            styles.standaloneBadge,
            {
              backgroundColor: badge.backgroundColor,
              borderColor: badge.accentColor,
            },
          ]}
          onPress={() => showComingSoon("Insignia")}
        >
          <Ionicons
            name={badge.medalIcon}
            size={16}
            color={badge.accentColor}
          />
          <Text style={[styles.medalText, { color: badge.accentColor }]}>
            {badge.label}
          </Text>
        </Pressable>

        <View style={styles.card}>
          <Text style={styles.cardLabel}>Categoría</Text>
          <Text style={styles.category}>{user.categoria ?? "PLATA"}</Text>
        </View>

        <Pressable
          style={styles.optionCard}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/financial-setup/cuenta-cobro" as any,
              params: { from: "profile" },
            })
          }
        >
          <Text style={styles.optionTitle}>Cuenta bancaria</Text>
          <Text style={styles.arrow}>→</Text>
        </Pressable>

        <Pressable
          style={styles.optionCard}
          onPress={() =>
            router.push({
              pathname: "/(tabs)/financial-setup/medios-pago" as any,
              params: { from: "profile" },
            })
          }
        >
          <Text style={styles.optionTitle}>Medios de pago</Text>
          <Text style={styles.arrow}>→</Text>
        </Pressable>

        <Pressable
          style={styles.optionCard}
          onPress={() => router.push("/(tabs)/compras" as any)}
        >
          <Text style={styles.optionTitle}>Mis compras</Text>
          <Text style={styles.arrow}>→</Text>
        </Pressable>
      </ScrollView>
    );
  }

  if (isValidated && !requiresPaymentSetup) {
    const badge = getBadgeConfig(user.categoria);

    return (
      <ScrollView
        style={styles.premiumScreen}
        contentContainerStyle={styles.premiumContent}
      >
        <View style={styles.heroCard}>
          <View style={styles.decorBubbleOne} />
          <View style={styles.decorBubbleTwo} />

          <View style={styles.heroUserRow}>
            <View style={styles.avatarCircle}>
              <Ionicons name="person" size={28} color="#334155" />
            </View>

            <View style={styles.userIdentityBlock}>
              <Pressable
                style={styles.textActionButton}
                onPress={() => showComingSoon("Usuario")}
              >
                <Text style={styles.userLabel}>Usuario</Text>
              </Pressable>

              <Pressable
                style={styles.textActionButton}
                onPress={() => showComingSoon("Nombre")}
              >
                <Text style={styles.userName}>{user.nombre}</Text>
              </Pressable>
            </View>

            <Pressable
              style={[
                styles.medalBadge,
                {
                  backgroundColor: badge.backgroundColor,
                  borderColor: badge.accentColor,
                },
              ]}
              onPress={() => showComingSoon("Insignia")}
            >
              <Ionicons
                name={badge.medalIcon}
                size={16}
                color={badge.accentColor}
              />
              <Text style={[styles.medalText, { color: badge.accentColor }]}>
                {badge.label}
              </Text>
            </Pressable>
          </View>

          <View style={styles.quickInfoRow}>
            <Pressable
              style={styles.quickInfoChip}
              onPress={() =>
                router.push("/(tabs)/financial-setup/tarjeta" as any)
              }
            >
              <Ionicons name="card-outline" size={14} color="#0F172A" />
              <Text style={styles.quickInfoText}>Tarjeta {tarjetaPreview}</Text>
            </Pressable>

            <Pressable
              style={styles.quickInfoChip}
              onPress={() =>
                router.push({
                  pathname: "/(tabs)/financial-setup/cuenta-cobro" as any,
                  params: { from: "profile" },
                })
              }
            >
              <Ionicons name="wallet-outline" size={14} color="#0F172A" />
              <Text style={styles.quickInfoText}>{cuentaPreview}</Text>
            </Pressable>
          </View>

          {financialLoading && (
            <Text style={styles.financialLoadingText}>
              Actualizando datos financieros...
            </Text>
          )}
        </View>

        <View style={styles.menuCard}>
          <Pressable
            style={styles.menuRowButton}
            onPress={() =>
              router.push({
                pathname: "/(tabs)/financial-setup/medios-pago" as any,
                params: { from: "profile" },
              })
            }
          >
            <Text style={styles.menuText}>Medios de pago</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() =>
              router.push({
                pathname: "/(tabs)/financial-setup/cuenta-cobro" as any,
                params: { from: "profile" },
              })
            }
          >
            <Text style={styles.menuText}>Cuenta bancaria</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() => router.push("/(tabs)/profile/publicaciones" as any)}
          >
            <Text style={styles.menuText}>Mis publicaciones</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() => router.push("/(tabs)/compras" as any)}
          >
            <Text style={styles.menuText}>Mis compras</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() => showComingSoon("Mis participaciones")}
          >
            <Text style={styles.menuText}>Mis participaciones</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() => showComingSoon("Estadísticas")}
          >
            <Text style={styles.menuText}>Estadísticas</Text>
            <Ionicons name="chevron-forward" size={18} color="#334155" />
          </Pressable>

          <Pressable
            style={styles.menuRowButton}
            onPress={() =>
              router.push("/(tabs)/profile/publicar/categoria" as any)
            }
          >
            <Text style={styles.menuText}>Publicar item</Text>
            <Ionicons name="cart-outline" size={20} color="#334155" />
          </Pressable>
        </View>

        <Pressable style={styles.logoutButton} onPress={handleLogout}>
          <Text style={styles.logoutText}>Cerrar sesión</Text>
        </Pressable>
      </ScrollView>
    );
  }

  const badge = getBadgeConfig(user.categoria);

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Hola, {user.nombre}</Text>
      <Text style={styles.subtitle}>{user.mail}</Text>

      <Pressable
        style={[
          styles.medalBadge,
          styles.standaloneBadge,
          {
            backgroundColor: badge.backgroundColor,
            borderColor: badge.accentColor,
          },
        ]}
        onPress={() => showComingSoon("Insignia")}
      >
        <Ionicons name={badge.medalIcon} size={16} color={badge.accentColor} />
        <Text style={[styles.medalText, { color: badge.accentColor }]}>
          {badge.label}
        </Text>
      </Pressable>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Estado de cuenta</Text>
        <Text style={styles.status}>Cuenta activa</Text>
        <Text style={styles.infoText}>
          Tu cuenta está lista para operar dentro de la app.
        </Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardLabel}>Categoría de postor</Text>
        <Text style={styles.category}>{user.categoria ?? "PLATA"}</Text>
      </View>

      <Pressable style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>Cerrar sesión</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#F5F6FA" },

  premiumScreen: {
    flex: 1,
    backgroundColor: "#F2F5FB",
  },

  premiumContent: {
    paddingHorizontal: 20,
    paddingTop: 24,
    paddingBottom: 40,
    gap: 16,
  },

  heroCard: {
    borderRadius: 26,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    padding: 18,
    overflow: "hidden",
  },

  decorBubbleOne: {
    position: "absolute",
    width: 140,
    height: 140,
    borderRadius: 70,
    backgroundColor: "#EAF1FF",
    top: -50,
    right: -40,
  },

  decorBubbleTwo: {
    position: "absolute",
    width: 110,
    height: 110,
    borderRadius: 55,
    backgroundColor: "#F2F7FF",
    bottom: -36,
    left: -30,
  },

  heroUserRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginBottom: 16,
  },

  avatarCircle: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: "#E9EFF8",
    borderWidth: 1,
    borderColor: "#CCD8EA",
    alignItems: "center",
    justifyContent: "center",
  },

  userIdentityBlock: {
    flex: 1,
    gap: 4,
  },

  textActionButton: {
    alignSelf: "flex-start",
  },

  userLabel: {
    fontSize: 14,
    color: "#475569",
    fontWeight: "700",
  },

  userName: {
    fontSize: 24,
    color: "#0F172A",
    fontWeight: "800",
  },

  medalBadge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 5,
    borderRadius: 999,
    borderWidth: 1,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },

  medalText: {
    fontSize: 13,
    fontWeight: "800",
  },

  standaloneBadge: {
    alignSelf: "flex-start",
    marginBottom: 14,
  },

  quickInfoRow: {
    flexDirection: "row",
    gap: 8,
  },

  quickInfoChip: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
    borderRadius: 12,
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#DCE4F2",
    paddingVertical: 8,
    paddingHorizontal: 10,
  },

  quickInfoText: {
    fontSize: 12,
    color: "#1E293B",
    fontWeight: "700",
  },

  financialLoadingText: {
    marginTop: 10,
    fontSize: 12,
    color: "#64748B",
    fontWeight: "600",
  },

  menuCard: {
    borderRadius: 20,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#DCE3F0",
    paddingVertical: 6,
  },

  menuRowButton: {
    minHeight: 50,
    paddingHorizontal: 16,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderBottomWidth: 1,
    borderBottomColor: "#EEF2F8",
  },

  menuText: {
    fontSize: 17,
    color: "#0F172A",
    fontWeight: "700",
  },

  container: {
    flexGrow: 1,
    paddingHorizontal: 24,
    paddingTop: 42,
    paddingBottom: 40,
  },

  stateContainer: {
    flex: 1,
    backgroundColor: "#2F63F6",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },

  pendingContainer: {
    flex: 1,
    backgroundColor: "#27447F",
    paddingHorizontal: 28,
    paddingTop: 70,
    paddingBottom: 34,
  },

  pendingTitle: {
    color: "white",
    fontSize: 27,
    fontWeight: "800",
    lineHeight: 36,
    marginBottom: 28,
  },

  pendingSubtitle: {
    color: "white",
    fontSize: 25,
    lineHeight: 34,
    marginBottom: 26,
  },

  pendingText: {
    marginTop: 10,
    color: "white",
    fontSize: 15,
  },

  mailText: {
    color: "#DBEAFE",
    fontSize: 15,
    fontWeight: "800",
    marginBottom: 38,
  },

  hourglass: {
    color: "white",
    fontSize: 78,
    textAlign: "center",
    marginBottom: 42,
  },

  transparentButton: {
    paddingVertical: 15,
    alignItems: "center",
    marginTop: 12,
  },

  transparentButtonText: {
    color: "white",
    fontSize: 15,
    fontWeight: "800",
  },

  title: {
    fontSize: 30,
    fontWeight: "900",
    color: "#111827",
    marginBottom: 10,
  },

  subtitle: {
    fontSize: 15,
    color: "#6B7280",
    lineHeight: 22,
    marginBottom: 24,
  },

  card: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
  },

  cardLabel: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "900",
    textTransform: "uppercase",
    letterSpacing: 1,
    marginBottom: 8,
  },

  status: {
    color: "#16A34A",
    fontSize: 20,
    fontWeight: "900",
    marginBottom: 8,
  },

  category: {
    fontSize: 22,
    color: "#111827",
    fontWeight: "900",
  },

  infoText: {
    fontSize: 14,
    color: "#4B5563",
    lineHeight: 21,
  },

  optionCard: {
    backgroundColor: "white",
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 14,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },

  optionTitle: {
    fontSize: 18,
    color: "#111827",
    fontWeight: "900",
  },

  arrow: {
    fontSize: 26,
    color: "#2F63F6",
    fontWeight: "900",
  },

  primaryButton: {
    backgroundColor: "#2F63F6",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
    marginTop: 8,
    marginBottom: 12,
  },

  primaryButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },

  secondaryButton: {
    backgroundColor: "white",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    marginBottom: 12,
  },

  secondaryButtonText: {
    color: "#111827",
    fontSize: 16,
    fontWeight: "800",
  },

  logoutButton: {
    backgroundColor: "#111827",
    paddingVertical: 15,
    borderRadius: 14,
    alignItems: "center",
  },

  logoutText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },
});
