import {
  eliminarSubastaGuardada,
  getSubastasGuardadas,
  guardarSubasta,
} from "@/src/api/subastasGuardadasAPI";
import { useAuth } from "@/src/context/authContext";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { AxiosError } from "axios";
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useMemo,
  useRef,
  useState,
} from "react";

type SubastasGuardadasContextType = {
  subastasGuardadas: SubastaHomeDTO[];
  idsGuardadas: Set<number>;
  pendingIds: Set<number>;
  loading: boolean;
  error: string | null;
  recargar: () => Promise<void>;
  guardar: (subasta: SubastaHomeDTO) => Promise<boolean>;
  eliminar: (idSubasta: number) => Promise<boolean>;
  toggleGuardada: (subasta: SubastaHomeDTO) => Promise<boolean>;
  estaGuardada: (idSubasta: number) => boolean;
};

const SubastasGuardadasContext =
  createContext<SubastasGuardadasContextType>({} as SubastasGuardadasContextType);

function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const data = error.response?.data as { message?: string } | string | undefined;

    if (typeof data === "string" && data.trim()) {
      return data;
    }

    if (typeof data === "object" && data?.message) {
      return data.message;
    }

    if (error.response?.status === 401) {
      return "Necesitás iniciar sesión para ver tus subastas guardadas.";
    }

    if (error.response?.status === 404) {
      return "No encontramos esa subasta.";
    }
  }

  return "No pudimos actualizar tus subastas guardadas.";
}

export function SubastasGuardadasProvider({
  children,
}: {
  children: ReactNode;
}) {
  const { isAuthenticated } = useAuth();
  const [subastasGuardadas, setSubastasGuardadas] = useState<SubastaHomeDTO[]>(
    [],
  );
  const [pendingIds, setPendingIds] = useState<Set<number>>(new Set());
  const pendingIdsRef = useRef<Set<number>>(new Set());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const idsGuardadas = useMemo(
    () => new Set(subastasGuardadas.map((subasta) => subasta.idSubasta)),
    [subastasGuardadas],
  );

  const estaGuardada = useCallback(
    (idSubasta: number) => idsGuardadas.has(idSubasta),
    [idsGuardadas],
  );

  const recargar = useCallback(async () => {
    if (!isAuthenticated) {
      setSubastasGuardadas([]);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSubastasGuardadas(await getSubastasGuardadas());
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  const runWithPending = useCallback(
    async (idSubasta: number, action: () => Promise<boolean>) => {
      if (pendingIdsRef.current.has(idSubasta)) {
        return estaGuardada(idSubasta);
      }

      pendingIdsRef.current.add(idSubasta);
      setPendingIds((current) => new Set(current).add(idSubasta));
      setError(null);

      try {
        return await action();
      } catch (err) {
        setError(getErrorMessage(err));
        return estaGuardada(idSubasta);
      } finally {
        pendingIdsRef.current.delete(idSubasta);
        setPendingIds((current) => {
          const next = new Set(current);
          next.delete(idSubasta);
          return next;
        });
      }
    },
    [estaGuardada],
  );

  const guardar = useCallback(
    async (subasta: SubastaHomeDTO) =>
      runWithPending(subasta.idSubasta, async () => {
        await guardarSubasta(subasta.idSubasta);
        setSubastasGuardadas((current) =>
          current.some((item) => item.idSubasta === subasta.idSubasta)
            ? current
            : [subasta, ...current],
        );
        return true;
      }),
    [runWithPending],
  );

  const eliminar = useCallback(
    async (idSubasta: number) =>
      runWithPending(idSubasta, async () => {
        await eliminarSubastaGuardada(idSubasta);
        setSubastasGuardadas((current) =>
          current.filter((subasta) => subasta.idSubasta !== idSubasta),
        );
        return false;
      }),
    [runWithPending],
  );

  const toggleGuardada = useCallback(
    async (subasta: SubastaHomeDTO) => {
      if (estaGuardada(subasta.idSubasta)) {
        return eliminar(subasta.idSubasta);
      }

      return guardar(subasta);
    },
    [eliminar, estaGuardada, guardar],
  );

  const value = useMemo(
    () => ({
      subastasGuardadas,
      idsGuardadas,
      pendingIds,
      loading,
      error,
      recargar,
      guardar,
      eliminar,
      toggleGuardada,
      estaGuardada,
    }),
    [
      eliminar,
      error,
      estaGuardada,
      guardar,
      idsGuardadas,
      loading,
      pendingIds,
      recargar,
      subastasGuardadas,
      toggleGuardada,
    ],
  );

  return (
    <SubastasGuardadasContext.Provider value={value}>
      {children}
    </SubastasGuardadasContext.Provider>
  );
}

export function useSubastasGuardadas() {
  return useContext(SubastasGuardadasContext);
}
