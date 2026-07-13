import { listarMisCompras } from "@/src/api/comprasAPI";
import { getApiErrorMessage } from "@/src/api/errors";
import { VentaResumenResponse } from "@/src/dto/CompraDTO";
import { useCallback, useEffect, useState } from "react";

export function useMisCompras() {
  const [compras, setCompras] = useState<VentaResumenResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      setCompras(await listarMisCompras());
    } catch (err) {
      setError(getApiErrorMessage(err, "No pudimos cargar tus compras."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  return { compras, loading, error, recargar };
}
