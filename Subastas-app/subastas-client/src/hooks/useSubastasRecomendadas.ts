import { useCallback, useEffect, useState } from "react";
import { getSubastasRecomendadas } from "@/src/api/subastaAPI";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";

export function useSubastasRecomendadas() {
  const [subastas, setSubastas] = useState<SubastaHomeDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarSubastas = useCallback(async (silent = false) => {
    try {
      if (!silent) setLoading(true);
      setError(null);

      const data = await getSubastasRecomendadas();

      setSubastas(data);
    } catch (err) {
      console.error("Error cargando subastas recomendadas:", err);
      setError("No pudimos cargar las subastas recomendadas.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    cargarSubastas();
  }, [cargarSubastas]);

  const recargarSilencioso = useCallback(
    () => cargarSubastas(true),
    [cargarSubastas],
  );

  const actualizarPrecioSubasta = useCallback(
    (idSubasta: number, precio: number) => {
      setSubastas((current) =>
        current.map((subasta) =>
          subasta.idSubasta === idSubasta ? { ...subasta, precio } : subasta,
        ),
      );
    },
    [],
  );

  return {
    subastas,
    loading,
    error,
    recargar: cargarSubastas,
    recargarSilencioso,
    actualizarPrecioSubasta,
  };
}
