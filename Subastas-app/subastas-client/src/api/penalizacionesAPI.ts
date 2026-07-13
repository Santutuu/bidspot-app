import api from "@/src/api/axios";
import {
  PagarPenalizacionRequest,
  PenalizacionResponse,
} from "@/src/dto/CompraDTO";

export async function listarMisPenalizaciones(): Promise<
  PenalizacionResponse[]
> {
  const response = await api.get<PenalizacionResponse[]>("/me/penalizaciones");
  return response.data;
}

export async function obtenerPenalizacion(
  idPenalizacion: number | string,
): Promise<PenalizacionResponse> {
  const response = await api.get<PenalizacionResponse>(
    `/me/penalizaciones/${idPenalizacion}`,
  );
  return response.data;
}

export async function pagarPenalizacion(
  idPenalizacion: number | string,
  request: PagarPenalizacionRequest,
): Promise<PenalizacionResponse> {
  const response = await api.post<PenalizacionResponse>(
    `/me/penalizaciones/${idPenalizacion}/pagar`,
    request,
  );
  return response.data;
}
