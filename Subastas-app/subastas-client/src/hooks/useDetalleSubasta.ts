import { getDetalleSubasta } from "@/src/api/subastaAPI";
import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";
import { router } from "expo-router";
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

export function useDetalleSubasta(id?: string) {
  const [detalle, setDetalle] = useState<DetalleSubastaDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [subastaFinalizada, setSubastaFinalizada] = useState(false);

  const cargarDetalle = useCallback(
    async (silent = false) => {
      if (!id) return;

      try {
        if (!silent) setLoading(true);
        setError(null);
        setSubastaFinalizada(false);

        const data = await getDetalleSubasta(id);
        setDetalle(data);
      } catch (err: any) {
        console.error("Error cargando detalle de subasta:", err);

        if (err.response?.status === 401) {
          router.replace("/(tabs)/profile");
          return;
        }

        if (err.response?.status === 403) {
          setError(
            err.response?.data?.message ??
              "No tenes categoria suficiente para acceder a esta subasta.",
          );
          return;
        }

        if (isSubastaFinalizadaError(err)) {
          setSubastaFinalizada(true);
          setError("Esta subasta finalizó o ya no está disponible.");
          return;
        }

        setError("No pudimos cargar el detalle de la subasta.");
      } finally {
        setLoading(false);
      }
    },
    [id],
  );

  useEffect(() => {
    cargarDetalle();
  }, [cargarDetalle]);

  const recargarSilencioso = useCallback(
    () => cargarDetalle(true),
    [cargarDetalle],
  );

  return {
    detalle,
    loading,
    error,
    subastaFinalizada,
    recargar: cargarDetalle,
    recargarSilencioso,
  };
}
