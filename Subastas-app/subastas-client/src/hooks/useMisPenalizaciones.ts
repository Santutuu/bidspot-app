import { getApiErrorMessage } from "@/src/api/errors";
import { listarMisPenalizaciones } from "@/src/api/penalizacionesAPI";
import { PenalizacionResponse } from "@/src/dto/CompraDTO";
import { useCallback, useEffect, useMemo, useState } from "react";

export function useMisPenalizaciones() {
  const [penalizaciones, setPenalizaciones] = useState<PenalizacionResponse[]>(
    [],
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      setPenalizaciones(await listarMisPenalizaciones());
    } catch (err) {
      setError(
        getApiErrorMessage(err, "No pudimos cargar tus penalizaciones."),
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  const penalizacionesPendientes = useMemo(
    () =>
      penalizaciones.filter(
        (penalizacion) => penalizacion.estado === "PENDIENTE",
      ),
    [penalizaciones],
  );

  return { penalizaciones, penalizacionesPendientes, loading, error, recargar };
}
