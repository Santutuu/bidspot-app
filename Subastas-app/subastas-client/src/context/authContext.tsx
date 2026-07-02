import { getCurrentUser } from "@/src/api/authAPI";
import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";
import { AuthUser } from "@/src/dto/auth/AuthUser";
import {
  clearAuthData,
  getStoredToken,
  getStoredUser,
  saveAuthData,
} from "@/src/storage/authStorage";
import {
  clearPendingRegistrationMail,
  getPendingRegistrationMail,
  savePendingRegistrationMail,
} from "@/src/storage/registrationFlowStorage";
import { createContext, ReactNode, useContext, useEffect, useState } from "react";

type AuthContextType = {
  user: AuthUser | null;
  token: string | null;
  loadingAuth: boolean;
  pendingRegistrationMail: string | null;

  isAuthenticated: boolean;
  isValidated: boolean;
  isBlocked: boolean;
  isRejected: boolean;
  isAdmin: boolean;
  hasGeneratedPassword: boolean;
  requiresPaymentSetup: boolean;

  login: (response: AuthResponseDTO) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
  setPendingMail: (mail: string) => Promise<void>;
  clearPendingMail: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [pendingRegistrationMail, setPendingRegistrationMail] =
    useState<string | null>(null);
  const [loadingAuth, setLoadingAuth] = useState(true);

  useEffect(() => {
    async function loadAuth() {
      try {
        const savedToken = await getStoredToken();
        const savedUser = await getStoredUser();

        if (savedToken && savedUser) {
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

          return;
        }

        setToken(null);
        setUser(null);

        const savedPendingMail = await getPendingRegistrationMail();
        setPendingRegistrationMail(savedPendingMail);
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
      categoria: response.categoria ?? null,
      claveGenerada: true,
      requiereMedioDePago:
        response.requiereMedioDePago ??
        !(
          response.configuracionFinancieraCompleta ??
          response.tieneMedioPago ??
          false
        ),
    };

    setToken(response.token);
    setUser(authUser);
    setPendingRegistrationMail(null);

    await clearPendingRegistrationMail();
    await saveAuthData(response.token, authUser);
  }

  async function logout() {
    setToken(null);
    setUser(null);
    setPendingRegistrationMail(null);

    await clearAuthData();
    await clearPendingRegistrationMail();
  }

  async function refreshUser() {
    const freshUser = await getCurrentUser();
    const savedToken = await getStoredToken();

    setUser(freshUser);

    if (savedToken) {
      await saveAuthData(savedToken, freshUser);
    }
  }

  async function setPendingMail(mail: string) {
    const normalized = mail.trim().toLowerCase();

    setToken(null);
    setUser(null);
    setPendingRegistrationMail(normalized);

    await clearAuthData();
    await savePendingRegistrationMail(normalized);
  }

  async function clearPendingMail() {
    setPendingRegistrationMail(null);
    await clearPendingRegistrationMail();
  }

  const isAuthenticated = !!token && !!user;
  const isValidated = user?.estado === "VALIDADO";
  const isBlocked = user?.estado === "BLOQUEADO";
  const isRejected = user?.estado === "RECHAZADO";
  const isAdmin = user?.rol === "ADMIN";
  const hasGeneratedPassword = !!user?.claveGenerada || isAuthenticated;
  const requiresPaymentSetup = !!user?.requiereMedioDePago;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loadingAuth,
        pendingRegistrationMail,
        isAuthenticated,
        isValidated,
        isBlocked,
        isRejected,
        isAdmin,
        hasGeneratedPassword,
        requiresPaymentSetup,
        login,
        logout,
        refreshUser,
        setPendingMail,
        clearPendingMail,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}