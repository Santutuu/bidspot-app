export interface DetalleSubastaDTO {
  idArticulo: number;
  idSubasta: number;

  titulo: string;
  descripcion: string;
  imagenesUrl: string[];

  moneda: string;

  precioActual: number | null;
  precioInicial: number;
  precioMostrado: number;
  tipoPrecio: "PRECIO_ACTUAL" | "PRECIO_INICIAL" | "PRECIO_FINAL";

  estadoSubasta: "CREADA" | "ACTIVA" | "FINALIZADA" | "CANCELADA";
  puedeOfertar: boolean;

  fechaInicio: string | null;
  horaInicio: string | null;

  martillero: string;
  linkStreaming: string;
}