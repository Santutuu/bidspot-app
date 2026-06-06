import api from "./axios";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";

export async function getSubastasRecomendadas(): Promise<
  SubastaHomeDTO[]
> {
  const response = await api.get<SubastaHomeDTO[]>(
    "/subastas/recomendadas"
  );

  return response.data;
}