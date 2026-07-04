import api from "./axios";

import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";
import {
  EstadoPujaSubastaResponseDTO,
  PujaResponseDTO,
} from "@/src/dto/PujaDTO";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { SubastasPorCategoriaDTO } from "@/src/dto/SubastasPorCategoriaDTO";

export async function getSubastasRecomendadas(): Promise<SubastaHomeDTO[]> {
  const response = await api.get<SubastaHomeDTO[]>("/subastas/recomendadas");

  return response.data;
}

export async function getSubastasPorCategoria(
  categoria: string
): Promise<SubastasPorCategoriaDTO> {
  const response = await api.get<SubastasPorCategoriaDTO>(
    `/subastas/categoria/${categoria}`
  );

  return response.data;
}

export async function getDetalleSubasta(
  id: string | number
): Promise<DetalleSubastaDTO> {
  const response = await api.get<DetalleSubastaDTO>(`/subastas/${id}`);

  return response.data;
}

export async function getEstadoPuja(
  idSubasta: string | number
): Promise<EstadoPujaSubastaResponseDTO> {
  const response = await api.get<EstadoPujaSubastaResponseDTO>(
    `/subastas/${idSubasta}/puja/estado`
  );

  return response.data;
}

export async function realizarPuja(
  idSubasta: string | number,
  monto: number
): Promise<PujaResponseDTO> {
  const response = await api.post<PujaResponseDTO>(
    `/subastas/${idSubasta}/pujas`,
    { monto }
  );

  return response.data;
}
