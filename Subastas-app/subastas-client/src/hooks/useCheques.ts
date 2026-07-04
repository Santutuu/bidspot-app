import { obtenerCheques } from "@/src/api/meAPI";
import { ChequeResponseDTO } from "@/src/dto/me/ChequeDTO";
import { useCallback, useState } from "react";

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos cargar los cheques."
  );
}

export function useCheques() {
  const [cheques, setCheques] = useState<ChequeResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const cargarCheques = useCallback(async (asRefresh = false) => {
    try {
      if (asRefresh) setRefreshing(true);
      else setLoading(true);

      setError(null);
      setCheques(await obtenerCheques());
    } catch (err: any) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  return {
    cheques,
    loading,
    refreshing,
    error,
    cargarCheques,
    refrescar: () => cargarCheques(true),
  };
}
