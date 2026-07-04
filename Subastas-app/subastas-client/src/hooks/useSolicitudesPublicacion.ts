import {
  crearSolicitudPublicacion,
  obtenerDetalleSolicitudPublicacion,
  obtenerMisSolicitudesPublicacion,
  responderAccionSolicitud,
} from "@/src/api/solicitudesPublicacionAPI";
import {
  AccionRequerida,
  ResponderAccionRequest,
  SolicitudPublicacionDetalle,
  SolicitudPublicacionRequest,
  SolicitudPublicacionResumen,
} from "@/src/types/solicitudesPublicacion";
import { router, useFocusEffect } from "expo-router";
import { useCallback, useEffect, useState } from "react";

function getMessage(error: any, fallback: string) {
  return error.response?.data?.message ?? error.response?.data?.error ?? fallback;
}

function handleAuthError(error: any) {
  if (error.response?.status === 401) {
    router.replace("/(tabs)/profile");
    return true;
  }

  return false;
}

export function useMisSolicitudesPublicacion() {
  const [solicitudes, setSolicitudes] = useState<SolicitudPublicacionResumen[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      setSolicitudes(await obtenerMisSolicitudesPublicacion());
    } catch (err: any) {
      if (handleAuthError(err)) return;
      setError(
        err.response?.status === 403
          ? "No tenés permisos para ver tus publicaciones."
          : getMessage(err, "No pudimos cargar tus publicaciones."),
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      void cargar();
    }, [cargar]),
  );

  return { solicitudes, loading, error, recargar: cargar };
}

export function useDetalleSolicitudPublicacion(idSolicitud?: string) {
  const [detalle, setDetalle] = useState<SolicitudPublicacionDetalle | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    if (!idSolicitud) return;

    try {
      setLoading(true);
      setError(null);
      setDetalle(await obtenerDetalleSolicitudPublicacion(idSolicitud));
    } catch (err: any) {
      if (handleAuthError(err)) return;
      setError(
        err.response?.status === 403
          ? "No tenés permisos para ver esta publicación."
          : getMessage(err, "No pudimos cargar la publicación."),
      );
    } finally {
      setLoading(false);
    }
  }, [idSolicitud]);

  useEffect(() => {
    void cargar();
  }, [cargar]);

  useFocusEffect(
    useCallback(() => {
      void cargar();
    }, [cargar]),
  );

  return { detalle, loading, error, recargar: cargar, setDetalle };
}

export function useCrearSolicitudPublicacion() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function crear(request: SolicitudPublicacionRequest) {
    try {
      setLoading(true);
      setError(null);
      return await crearSolicitudPublicacion(request);
    } catch (err: any) {
      if (handleAuthError(err)) return null;
      const message =
        err.response?.status === 403
          ? "No tenés permisos para crear publicaciones."
          : getMessage(err, "No pudimos crear la publicación.");
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }

  return { crear, loading, error };
}

export function useResponderAccionSolicitud() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function responder(
    idSolicitud: string | number,
    accion: AccionRequerida,
    request: ResponderAccionRequest,
  ) {
    try {
      setLoading(true);
      setError(null);
      return await responderAccionSolicitud(idSolicitud, accion, request);
    } catch (err: any) {
      if (handleAuthError(err)) return null;
      const message =
        err.response?.status === 403
          ? "No tenés permisos para responder esta acción."
          : getMessage(err, "No pudimos responder la acción.");
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }

  return { responder, loading, error };
}
