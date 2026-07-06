import { obtenerDetalleCompra } from "@/src/api/comprasAPI";
import { VentaDetalleResponse } from "@/src/dto/CompraDTO";
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

  return "No pudimos cargar el detalle de la compra.";
}

export function useCompraDetalle(idVenta?: string | number) {
  const [compra, setCompra] = useState<VentaDetalleResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    if (!idVenta) return;

    try {
      setLoading(true);
      setError(null);
      setCompra(await obtenerDetalleCompra(idVenta));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [idVenta]);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  return { compra, loading, error, recargar, setCompra };
}
