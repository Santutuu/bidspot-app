import api from "@/src/api/axios";
import { UltimaAdjudicacionResponse } from "@/src/dto/CompraDTO";

export async function obtenerUltimaAdjudicacion(): Promise<
  UltimaAdjudicacionResponse
> {
  const response = await api.get<UltimaAdjudicacionResponse>(
    "/me/mensajeria/ultima-adjudicacion",
  );
  return response.data;
}
