import { createContext, useContext, useEffect, useState } from "react";

import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";

import {
  getToken,
  removeToken,
  saveToken,
} from "@/src/storage/tokenStorage";

type AuthContextType = {
  user: AuthResponseDTO | null;
  token: string | null;
  isAuthenticated: boolean;

  login: (
    user: AuthResponseDTO
  ) => Promise<void>;

  logout: () => Promise<void>;
};

const AuthContext =
  createContext<AuthContextType>(
    {} as AuthContextType
  );

export function AuthProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [user, setUser] =
    useState<AuthResponseDTO | null>(
      null
    );

  const [token, setToken] =
    useState<string | null>(null);

  useEffect(() => {
    async function loadToken() {
      const savedToken =
        await getToken();

      if (savedToken) {
        setToken(savedToken);
      }
    }

    loadToken();
  }, []);

  async function login(
    authResponse: AuthResponseDTO
  ) {
    setUser(authResponse);
    setToken(authResponse.token);

    await saveToken(
      authResponse.token
    );
  }

  async function logout() {
    setUser(null);
    setToken(null);

    await removeToken();
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated:
          token !== null,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(
    AuthContext
  );
}