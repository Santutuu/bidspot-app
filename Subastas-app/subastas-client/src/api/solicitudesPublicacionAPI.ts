import api from "@/src/api/axios";
import {
  AumentarPolizaRequest,
  ConfigurarDevolucionRequest,
  PolizaSolicitudResponse,
  ResponderAccionRequest,
  SolicitudPublicacionDetalle,
  SolicitudPublicacionRequest,
  SolicitudPublicacionResumen,
} from "@/src/types/solicitudesPublicacion";

export async function crearSolicitudPublicacion(
  request: SolicitudPublicacionRequest,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.post<SolicitudPublicacionDetalle>(
    "/me/solicitudes-publicacion",
    request,
  );

  return response.data;
}

export async function obtenerMisSolicitudesPublicacion(): Promise<
  SolicitudPublicacionResumen[]
> {
  const response = await api.get<SolicitudPublicacionResumen[]>(
    "/me/solicitudes-publicacion",
  );

  return response.data;
}

export async function obtenerDetalleSolicitudPublicacion(
  idSolicitud: string | number,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.get<SolicitudPublicacionDetalle>(
    `/me/solicitudes-publicacion/${idSolicitud}`,
  );

  return response.data;
}

export async function cancelarSolicitudPublicacion(
  idSolicitud: string | number,
): Promise<void> {
  await api.delete(`/me/solicitudes-publicacion/${idSolicitud}`);
}

export async function responderAccionSolicitud(
  idSolicitud: string | number,
  idAccion: string | number,
  request: ResponderAccionRequest,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.post<SolicitudPublicacionDetalle>(
    `/me/solicitudes-publicacion/${idSolicitud}/acciones/${idAccion}/resolver`,
    request,
  );

  return response.data;
}

export async function configurarDevolucionSolicitud(
  idSolicitud: string | number,
  request: ConfigurarDevolucionRequest,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.put<SolicitudPublicacionDetalle>(
    `/me/solicitudes-publicacion/${idSolicitud}/devolucion`,
    request,
  );

  return response.data;
}

export async function confirmarPagoDevolucionSolicitud(
  idSolicitud: string | number,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.post<SolicitudPublicacionDetalle>(
    `/me/solicitudes-publicacion/${idSolicitud}/devolucion/confirmar-pago`,
  );

  return response.data;
}

export async function obtenerPolizaSolicitud(
  idSolicitud: string | number,
): Promise<PolizaSolicitudResponse> {
  const response = await api.get<PolizaSolicitudResponse>(
    `/me/solicitudes-publicacion/${idSolicitud}/poliza`,
  );

  return response.data;
}

export async function aumentarPolizaSolicitud(
  idSolicitud: string | number,
  nuevoMontoAsegurado: number,
): Promise<PolizaSolicitudResponse> {
  const request: AumentarPolizaRequest = { nuevoMontoAsegurado };
  const response = await api.post<PolizaSolicitudResponse>(
    `/me/solicitudes-publicacion/${idSolicitud}/poliza/aumentar`,
    request,
  );

  return response.data;
}

export async function aceptarPolizaSolicitud(
  idSolicitud: string | number,
): Promise<PolizaSolicitudResponse> {
  const response = await api.post<PolizaSolicitudResponse>(
    `/me/solicitudes-publicacion/${idSolicitud}/poliza/aceptar`,
  );

  return response.data;
}

export async function rechazarPolizaSolicitud(
  idSolicitud: string | number,
): Promise<PolizaSolicitudResponse> {
  const response = await api.post<PolizaSolicitudResponse>(
    `/me/solicitudes-publicacion/${idSolicitud}/poliza/rechazar`,
  );

  return response.data;
}
