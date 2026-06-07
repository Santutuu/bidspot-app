import { useEffect, useState } from "react";
import { getDetalleSubasta } from "@/src/api/subastaAPI";
import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";

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
    } catch (err) {
      console.error("Error cargando detalle de subasta:", err);
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