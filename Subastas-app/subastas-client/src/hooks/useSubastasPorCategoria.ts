import { useEffect, useState } from "react";

import { getSubastasPorCategoria } from "@/src/api/subastaAPI";
import { SubastasPorCategoriaDTO } from "@/src/dto/SubastasPorCategoriaDTO";

export function useSubastasPorCategoria(categoria?: string) {
  const [data, setData] = useState<SubastasPorCategoriaDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function cargarSubastas() {
    if (!categoria) return;

    try {
      setLoading(true);
      setError(null);

      const response = await getSubastasPorCategoria(categoria);

      setData(response);
    } catch (err) {
      console.error("Error cargando subastas por categoría:", err);
      setError("No pudimos cargar las subastas de esta categoría.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    cargarSubastas();
  }, [categoria]);

  return {
    data,
    loading,
    error,
    recargar: cargarSubastas,
  };
}