import { useEffect, useState } from "react";
import { getDetalleSubasta } from "@/src/api/subastaAPI";
import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";
import { router } from "expo-router";

export function useDetalleSubasta(id?: string) {
  const [detalle, setDetalle] = useState<DetalleSubastaDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function cargarDetalle() {
    if (!id) return;

    try {
      setLoading(true);
      setError(null);

      const data = await getDetalleSubasta(id);
      setDetalle(data);
    } catch (err: any) {
      console.error("Error cargando detalle de subasta:", err);

      if (err.response?.status === 401) {
        router.replace("/(tabs)/profile");
        return;
      }

      if (err.response?.status === 403) {
        setError("No tenés categoría suficiente para acceder a esta subasta.");
        return;
      }

      setError("No pudimos cargar el detalle de la subasta.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    cargarDetalle();
  }, [id]);

  return {
    detalle,
    loading,
    error,
    recargar: cargarDetalle,
  };
}
