export interface SubastaHomeDTO {
  id: number;
  titulo: string;
  moneda: string;
  precioActual: number | null;
  precioVisible: boolean;
  imagenUrl: string | null;
}