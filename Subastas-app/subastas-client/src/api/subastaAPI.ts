import api from "./axios";

import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";
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