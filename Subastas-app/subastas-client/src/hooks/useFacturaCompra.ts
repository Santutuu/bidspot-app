import { obtenerFacturaCompra } from "@/src/api/comprasAPI";
import { getApiErrorMessage } from "@/src/api/errors";
import { FacturaResponse } from "@/src/dto/CompraDTO";
import { useCallback, useEffect, useState } from "react";

export function useFacturaCompra(idVenta?: string | number) {
  const [factura, setFactura] = useState<FacturaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    if (!idVenta || Number.isNaN(Number(idVenta))) {
      setError("No pudimos identificar la compra.");
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setFactura(await obtenerFacturaCompra(idVenta));
    } catch (err) {
      setError(getApiErrorMessage(err, "No pudimos cargar la factura."));
    } finally {
      setLoading(false);
    }
  }, [idVenta]);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  return { factura, loading, error, recargar };
}
