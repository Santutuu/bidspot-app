export interface SubastaHomeDTO {
  idSubasta: number;
  titulo: string;
  imagenUrl: string | null;
  precio: number | null;
  moneda: string;
  estadoSubasta: "PROGRAMADA" | "ACTIVA" | "FINALIZADA" | "CANCELADA";
  categoriaMin: string;
  fechaInicio: string | null;
}
