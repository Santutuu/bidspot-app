import { listarMisCompras } from "@/src/api/comprasAPI";
import { VentaResumenResponse } from "@/src/dto/CompraDTO";
import { AxiosError } from "axios";
import { useCallback, useEffect, useState } from "react";

function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const data = error.response?.data as
      | { message?: string; error?: string }
      | string
      | undefined;
    const backendMessage =
      typeof data === "string" ? data : (data?.message ?? data?.error);

    if (backendMessage) return backendMessage;
  }

  return "No pudimos cargar tus compras.";
}

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
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  return { compras, loading, error, recargar };
}
