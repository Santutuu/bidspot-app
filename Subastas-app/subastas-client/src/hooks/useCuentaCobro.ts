import { obtenerCuentaCobro } from "@/src/api/meAPI";
import { CuentaCobroResponseDTO } from "@/src/dto/me/CuentaCobroDTO";
import { useCallback, useState } from "react";

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos cargar la cuenta de cobro."
  );
}

export function useCuentaCobro() {
  const [cuenta, setCuenta] = useState<CuentaCobroResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const cargarCuenta = useCallback(async (asRefresh = false) => {
    try {
      if (asRefresh) setRefreshing(true);
      else setLoading(true);

      setError(null);

      const data = await obtenerCuentaCobro();
      setCuenta(data);
    } catch (err: any) {
      if (err.response?.status === 404) {
        setCuenta(null);
        setError(null);
        return;
      }

      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  return {
    cuenta,
    loading,
    refreshing,
    error,
    cargarCuenta,
    refrescar: () => cargarCuenta(true),
  };
}
