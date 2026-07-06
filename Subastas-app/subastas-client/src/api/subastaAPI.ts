import api from "./axios";

import { DetalleSubastaDTO } from "@/src/dto/DetalleSubastaDTO";
import {
  EstadoPujaSubastaResponseDTO,
  PujaResponseDTO,
} from "@/src/dto/PujaDTO";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { SubastasPorCategoriaDTO } from "@/src/dto/SubastasPorCategoriaDTO";

export interface CierreLoteResponseDTO {
  idSubasta: number;
  idItemCatalogo: number;
  idVenta: number | null;
  idGanador: number | null;
  estadoLote: string;
  estadoSubasta: string;
  idProximoLote: number | null;
}

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

export async function cerrarLoteDemo(
  idSubasta: string | number,
  idItemCatalogo: string | number
): Promise<CierreLoteResponseDTO> {
  const response = await api.post<CierreLoteResponseDTO>(
    `/subastas/${idSubasta}/lotes/${idItemCatalogo}/cerrar`
  );

  return response.data;
}
