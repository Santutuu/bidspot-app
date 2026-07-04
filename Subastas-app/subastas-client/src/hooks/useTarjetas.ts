import { obtenerTarjetas } from "@/src/api/meAPI";
import { TarjetaResponseDTO } from "@/src/dto/me/TarjetaDTO";
import { useCallback, useState } from "react";

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos cargar las tarjetas."
  );
}

export function useTarjetas() {
  const [tarjetas, setTarjetas] = useState<TarjetaResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const cargarTarjetas = useCallback(async (asRefresh = false) => {
    try {
      if (asRefresh) setRefreshing(true);
      else setLoading(true);

      setError(null);
      setTarjetas(await obtenerTarjetas());
    } catch (err: any) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  return {
    tarjetas,
    loading,
    refreshing,
    error,
    cargarTarjetas,
    refrescar: () => cargarTarjetas(true),
  };
}
