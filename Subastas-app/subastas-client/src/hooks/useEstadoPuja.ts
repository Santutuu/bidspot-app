import { getEstadoPuja } from "@/src/api/subastaAPI";
import { EstadoPujaSubastaResponseDTO } from "@/src/dto/PujaDTO";
import { useCallback, useEffect, useState } from "react";

function isSubastaFinalizadaError(error: any) {
  const status = error?.response?.status;
  const backendMessage = String(
    error?.response?.data?.message ?? error?.response?.data?.error ?? "",
  ).toLowerCase();

  if (status !== 404 && status !== 409) {
    return false;
  }

  return (
    backendMessage.includes("no existe") ||
    backendMessage.includes("no activa") ||
    backendMessage.includes("finalizada") ||
    backendMessage.includes("cerrada")
  );
}

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos cargar el estado de la puja."
  );
}

export function useEstadoPuja(idSubasta?: string) {
  const [estadoPuja, setEstadoPuja] =
    useState<EstadoPujaSubastaResponseDTO | null>(null);
  const [loadingEstadoPuja, setLoadingEstadoPuja] = useState(false);
  const [errorEstadoPuja, setErrorEstadoPuja] = useState<string | null>(null);
  const [subastaFinalizada, setSubastaFinalizada] = useState(false);

  const cargarEstadoPuja = useCallback(
    async (silent = false) => {
      if (!idSubasta) return;

      try {
        if (!silent) setLoadingEstadoPuja(true);
        setErrorEstadoPuja(null);
        setSubastaFinalizada(false);
        setEstadoPuja(await getEstadoPuja(idSubasta));
      } catch (error: any) {
        if (isSubastaFinalizadaError(error)) {
          setSubastaFinalizada(true);
          setErrorEstadoPuja("Esta subasta finalizó o ya no está disponible.");
          return;
        }

        setErrorEstadoPuja(getErrorMessage(error));
      } finally {
        setLoadingEstadoPuja(false);
      }
    },
    [idSubasta],
  );

  useEffect(() => {
    void cargarEstadoPuja();
  }, [cargarEstadoPuja]);

  const cargarEstadoPujaSilencioso = useCallback(
    () => cargarEstadoPuja(true),
    [cargarEstadoPuja],
  );

  return {
    estadoPuja,
    loadingEstadoPuja,
    errorEstadoPuja,
    subastaFinalizada,
    cargarEstadoPuja,
    cargarEstadoPujaSilencioso,
  };
}
