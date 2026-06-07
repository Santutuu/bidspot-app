import { useEffect, useState } from "react";
import { getSubastasRecomendadas } from "@/src/api/subastaAPI";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";

export function useSubastasRecomendadas() {
  const [subastas, setSubastas] = useState<SubastaHomeDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function cargarSubastas() {
    try {
      setLoading(true);
      setError(null);

      const data = await getSubastasRecomendadas();

      setSubastas(data);
    } catch (err) {
      console.error("Error cargando subastas recomendadas:", err);
      setError("No pudimos cargar las subastas recomendadas.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    cargarSubastas();
  }, []);

  return {
    subastas,
    loading,
    error,
    recargar: cargarSubastas,
  };
}