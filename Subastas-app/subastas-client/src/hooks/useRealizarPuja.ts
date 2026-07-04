import { realizarPuja } from "@/src/api/subastaAPI";
import { PujaResponseDTO } from "@/src/dto/PujaDTO";
import { useCallback, useState } from "react";

function getErrorMessage(error: any) {
  return (
    error.response?.data?.message ??
    error.response?.data?.error ??
    "No pudimos registrar la puja."
  );
}

export function useRealizarPuja(idSubasta?: string) {
  const [submittingPuja, setSubmittingPuja] = useState(false);
  const [errorPuja, setErrorPuja] = useState<string | null>(null);
  const [successPuja, setSuccessPuja] = useState<string | null>(null);

  const enviarPuja = useCallback(
    async (monto: number): Promise<PujaResponseDTO | null> => {
      if (!idSubasta) return null;

      try {
        setSubmittingPuja(true);
        setErrorPuja(null);
        setSuccessPuja(null);

        const response = await realizarPuja(idSubasta, monto);
        setSuccessPuja("Tu puja fue registrada correctamente.");

        return response;
      } catch (error: any) {
        setErrorPuja(getErrorMessage(error));
        return null;
      } finally {
        setSubmittingPuja(false);
      }
    },
    [idSubasta],
  );

  return {
    submittingPuja,
    errorPuja,
    successPuja,
    setErrorPuja,
    setSuccessPuja,
    enviarPuja,
  };
}
