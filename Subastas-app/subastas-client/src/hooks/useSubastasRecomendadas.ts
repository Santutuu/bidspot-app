import { useCallback, useEffect, useState } from "react";
import { getSubastasRecomendadas } from "@/src/api/subastaAPI";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";

let sessionRecommendedSubastas: SubastaHomeDTO[] | null = null;

function mergeKeepingSessionOrder(
  current: SubastaHomeDTO[],
  latest: SubastaHomeDTO[],
) {
  const latestById = new Map(
    latest.map((subasta) => [subasta.idSubasta, subasta]),
  );

  return current.map((subasta) => latestById.get(subasta.idSubasta) ?? subasta);
}

export function useSubastasRecomendadas() {
  const [subastas, setSubastas] = useState<SubastaHomeDTO[]>(
    sessionRecommendedSubastas ?? [],
  );
  const [loading, setLoading] = useState(!sessionRecommendedSubastas);
  const [error, setError] = useState<string | null>(null);

  const cargarSubastas = useCallback(async (silent = false) => {
    try {
      if (!silent) setLoading(true);
      setError(null);

      const data = await getSubastasRecomendadas();

      if (!sessionRecommendedSubastas) {
        sessionRecommendedSubastas = data;
      } else {
        sessionRecommendedSubastas = mergeKeepingSessionOrder(
          sessionRecommendedSubastas,
          data,
        );
      }

      setSubastas(sessionRecommendedSubastas);
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
      sessionRecommendedSubastas = (sessionRecommendedSubastas ?? []).map(
        (subasta) =>
          subasta.idSubasta === idSubasta ? { ...subasta, precio } : subasta,
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
