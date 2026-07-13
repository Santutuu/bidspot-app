import { getApiErrorMessage } from "@/src/api/errors";
import { obtenerUltimaAdjudicacion } from "@/src/api/mensajeriaAPI";
import { UltimaAdjudicacionResponse } from "@/src/dto/CompraDTO";
import { useCallback, useEffect, useState } from "react";

export function useUltimaAdjudicacion() {
  const [adjudicacion, setAdjudicacion] =
    useState<UltimaAdjudicacionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      setAdjudicacion(await obtenerUltimaAdjudicacion());
    } catch (err) {
      setError(
        getApiErrorMessage(err, "No pudimos cargar la ultima adjudicacion."),
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  return { adjudicacion, loading, error, recargar };
}
