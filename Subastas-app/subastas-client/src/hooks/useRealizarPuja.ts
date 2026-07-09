import { realizarPuja } from "@/src/api/subastaAPI";
import { PujaResponseDTO } from "@/src/dto/PujaDTO";
import { AxiosError } from "axios";
import { useCallback, useRef, useState } from "react";
import { Alert } from "react-native";

const AUCTION_CONFLICT_MESSAGE =
  "No podes participar en mas de una subasta al mismo tiempo.";

function sanitizeErrorMessage(message: string) {
  return message
    .replace(/^\s*(error\s*)?\d{3}\s*[-:]?\s*/i, "")
    .replace(/^request failed with status code\s+\d{3}\s*[-:]?\s*/i, "")
    .replace(/^axioserror\s*:\s*/i, "")
    .trim();
}

function getBackendMessage(error: AxiosError) {
  const data = error.response?.data as
    | { message?: string; error?: string }
    | string
    | undefined;
  const rawMessage =
    typeof data === "string" ? data : (data?.message ?? data?.error);

  if (!rawMessage) {
    return undefined;
  }

  const sanitized = sanitizeErrorMessage(rawMessage);
  return sanitized.length > 0 ? sanitized : undefined;
}

function isSimultaneousAuctionError(error: unknown) {
  if (!(error instanceof AxiosError)) {
    return false;
  }

  const backendMessage = getBackendMessage(error);

  return (
    error.response?.status === 409 &&
    !!backendMessage?.toLowerCase().includes("subasta al mismo tiempo")
  );
}

function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const backendMessage = getBackendMessage(error);

    if (backendMessage) {
      return backendMessage;
    }
  }

  return "No pudimos registrar la puja.";
}

export function useRealizarPuja(idSubasta?: string) {
  const submittingRef = useRef(false);
  const [submittingPuja, setSubmittingPuja] = useState(false);
  const [errorPuja, setErrorPuja] = useState<string | null>(null);
  const [successPuja, setSuccessPuja] = useState<string | null>(null);

  const enviarPuja = useCallback(
    async (monto: number): Promise<PujaResponseDTO | null> => {
      if (!idSubasta) return null;
      if (submittingRef.current) return null;

      try {
        submittingRef.current = true;
        setSubmittingPuja(true);
        setErrorPuja(null);
        setSuccessPuja(null);

        const response = await realizarPuja(idSubasta, monto);
        Alert.alert("Puja registrada", "Tu puja fue registrada correctamente.");

        return response;
      } catch (error: unknown) {
        if (isSimultaneousAuctionError(error)) {
          Alert.alert("No podes participar", AUCTION_CONFLICT_MESSAGE);
          return null;
        }

        Alert.alert("No se pudo registrar la puja", getErrorMessage(error));
        return null;
      } finally {
        submittingRef.current = false;
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
