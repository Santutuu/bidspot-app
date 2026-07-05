import { obtenerMediosPago } from "@/src/api/meAPI";
import { MedioPagoResponseDTO } from "@/src/dto/me/MedioPagoDTO";
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

    if (backendMessage) {
      return backendMessage;
    }
  }

  return "No pudimos cargar tus medios de pago.";
}

export function useMediosPago(autoLoad = true) {
  const [mediosPago, setMediosPago] = useState<MedioPagoResponseDTO[]>([]);
  const [loadingMediosPago, setLoadingMediosPago] = useState(autoLoad);
  const [errorMediosPago, setErrorMediosPago] = useState<string | null>(null);

  const cargarMediosPago = useCallback(async () => {
    try {
      setLoadingMediosPago(true);
      setErrorMediosPago(null);
      setMediosPago(await obtenerMediosPago());
    } catch (error: unknown) {
      setErrorMediosPago(getErrorMessage(error));
    } finally {
      setLoadingMediosPago(false);
    }
  }, []);

  useEffect(() => {
    if (autoLoad) {
      void cargarMediosPago();
    }
  }, [autoLoad, cargarMediosPago]);

  return {
    mediosPago,
    loadingMediosPago,
    errorMediosPago,
    cargarMediosPago,
  };
}
