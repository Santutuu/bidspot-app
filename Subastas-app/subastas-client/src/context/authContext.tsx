import { getCurrentUser } from "@/src/api/authAPI";
import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";
import { AuthUser } from "@/src/dto/auth/AuthUser";
import {
  clearAuthData,
  getStoredToken,
  getStoredUser,
  saveAuthData,
} from "@/src/storage/authStorage";
import { createContext, ReactNode, useContext, useEffect, useState } from "react";

type AuthContextType = {
  user: AuthUser | null;
  token: string | null;
  loadingAuth: boolean;

  isAuthenticated: boolean;
  isValidated: boolean;
  isBlocked: boolean;
  isRejected: boolean;
  isAdmin: boolean;

  login: (response: AuthResponseDTO) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loadingAuth, setLoadingAuth] = useState(true);

  useEffect(() => {
    async function loadAuth() {
      try {
        const savedToken = await getStoredToken();
        const savedUser = await getStoredUser();

        if (!savedToken || !savedUser) {
          setToken(null);
          setUser(null);
          return;
        }

        setToken(savedToken);
        setUser(savedUser);

        try {
          const freshUser = await getCurrentUser();
          setUser(freshUser);
          await saveAuthData(savedToken, freshUser);
        } catch {
          await clearAuthData();
          setToken(null);
          setUser(null);
        }
      } finally {
        setLoadingAuth(false);
      }
    }

    loadAuth();
  }, []);

  async function login(response: AuthResponseDTO) {
    const authUser: AuthUser = {
      idUsuario: response.idUsuario,
      nombre: response.nombre,
      mail: response.mail,
      rol: response.rol,
      estado: response.estado,
    };

    setToken(response.token);
    setUser(authUser);

    await saveAuthData(response.token, authUser);
  }

  async function logout() {
    setToken(null);
    setUser(null);
    await clearAuthData();
  }

  async function refreshUser() {
    const freshUser = await getCurrentUser();
    const savedToken = await getStoredToken();

    setUser(freshUser);

    if (savedToken) {
      await saveAuthData(savedToken, freshUser);
    }
  }

  const isAuthenticated = !!token && !!user;
  const isValidated = user?.estado === "VALIDADO";
  const isBlocked = user?.estado === "BLOQUEADO";
  const isRejected = user?.estado === "RECHAZADO";
  const isAdmin = user?.rol === "ADMIN";

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loadingAuth,
        isAuthenticated,
        isValidated,
        isBlocked,
        isRejected,
        isAdmin,
        login,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}