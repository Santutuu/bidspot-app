import { router } from "expo-router";
import { Alert } from "react-native";

type AuthGuardParams = {
  isAuthenticated: boolean;
  isValidated?: boolean;
  isBlocked?: boolean;
  isRejected?: boolean;
  isAdmin?: boolean;
};

export function requireLogin({ isAuthenticated }: AuthGuardParams) {
  if (!isAuthenticated) {
    Alert.alert(
      "Iniciá sesión",
      "Necesitás iniciar sesión para continuar.",
      [
        {
          text: "Cancelar",
          style: "cancel",
          onPress: () => router.back(),
        },
        {
          text: "Iniciar sesión",
          onPress: () => router.replace("/auth/login"),
        },
      ]
    );

    return false;
  }

  return true;
}

export function requireNotBlocked({
  isAuthenticated,
  isBlocked,
  isRejected,
}: AuthGuardParams) {
  if (!requireLogin({ isAuthenticated })) return false;

  if (isBlocked) {
    Alert.alert("Cuenta bloqueada", "Tu cuenta se encuentra bloqueada.");
    return false;
  }

  if (isRejected) {
    Alert.alert("Cuenta rechazada", "Tu solicitud fue rechazada.");
    return false;
  }

  return true;
}

export function requireValidatedUser({
  isAuthenticated,
  isValidated,
  isBlocked,
  isRejected,
}: AuthGuardParams) {
  if (!requireNotBlocked({ isAuthenticated, isBlocked, isRejected })) {
    return false;
  }

  if (!isValidated) {
    Alert.alert(
      "Cuenta pendiente de validación",
      "La empresa debe validar tu cuenta antes de que puedas realizar esta acción."
    );

    return false;
  }

  return true;
}

export function requireAdmin({ isAuthenticated, isAdmin }: AuthGuardParams) {
  if (!requireLogin({ isAuthenticated })) return false;

  if (!isAdmin) {
    Alert.alert("Acceso denegado", "No tenés permisos para acceder.");
    return false;
  }

  return true;
}