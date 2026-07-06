import api from "@/src/api/axios";
import {
  AccionRequerida,
  AumentarPolizaRequest,
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
  accion: AccionRequerida,
  request: ResponderAccionRequest,
): Promise<SolicitudPublicacionDetalle> {
  const response = await api.post<SolicitudPublicacionDetalle>(
    `/me/solicitudes-publicacion/${idSolicitud}/acciones/${accion}/resolver`,
    request,
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
