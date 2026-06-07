import { SubastaHomeDTO } from "./SubastaHomeDTO";

export interface SubastasPorCategoriaDTO {
  nombreCategoria: string;
  tiempoReal: SubastaHomeDTO[];
  programadas: SubastaHomeDTO[];
}