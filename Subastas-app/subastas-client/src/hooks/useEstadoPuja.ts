import { getEstadoPuja } from "@/src/api/subastaAPI";
import { EstadoPujaSubastaResponseDTO } from "@/src/dto/PujaDTO";
import { useCallback, useEffect, useState } from "react";

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

  const cargarEstadoPuja = useCallback(async (silent = false) => {
    if (!idSubasta) return;

    try {
      if (!silent) setLoadingEstadoPuja(true);
      setErrorEstadoPuja(null);
      setEstadoPuja(await getEstadoPuja(idSubasta));
    } catch (error: any) {
      setErrorEstadoPuja(getErrorMessage(error));
    } finally {
      setLoadingEstadoPuja(false);
    }
  }, [idSubasta]);

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
    cargarEstadoPuja,
    cargarEstadoPujaSilencioso,
  };
}
