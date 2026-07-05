import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";

import api from "./axios";

export async function getSubastasGuardadas(): Promise<SubastaHomeDTO[]> {
  const response = await api.get<SubastaHomeDTO[]>("/me/subastas-guardadas");
  return response.data;
}

export async function guardarSubasta(idSubasta: number): Promise<void> {
  await api.post(`/me/subastas-guardadas/${idSubasta}`);
}

export async function eliminarSubastaGuardada(
  idSubasta: number,
): Promise<void> {
  await api.delete(`/me/subastas-guardadas/${idSubasta}`);
}
