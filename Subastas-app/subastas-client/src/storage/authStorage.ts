import { AuthUser } from "@/src/dto/auth/AuthUser";
import * as SecureStore from "expo-secure-store";

const TOKEN_KEY = "auth_token";
const USER_KEY = "auth_user";

export async function saveAuthData(token: string, user: AuthUser) {
  await SecureStore.setItemAsync(TOKEN_KEY, token);
  await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));
}

export async function getStoredToken() {
  return await SecureStore.getItemAsync(TOKEN_KEY);
}

export async function getStoredUser(): Promise<AuthUser | null> {
  const userJson = await SecureStore.getItemAsync(USER_KEY);

  if (!userJson) {
    return null;
  }

  return JSON.parse(userJson);
}

export async function clearAuthData() {
  await SecureStore.deleteItemAsync(TOKEN_KEY);
  await SecureStore.deleteItemAsync(USER_KEY);
}