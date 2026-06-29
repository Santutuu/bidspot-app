export type EstadoSubasta =
  | "PROGRAMADA"
  | "ACTIVA"
  | "FINALIZADA"
  | "CANCELADA";

export interface SubastaDetalleInfoDTO {
  idSubasta: number;
  titulo: string;
  estadoSubasta: EstadoSubasta;
  categoriaMin: string;
  moneda: string;
  fechaInicio: string | null;
  ubicacion: string | null;
  rematador: string | { nombre?: string; apellido?: string } | null;
  linkVivo: string | null;
}

export interface ItemActualDTO {
  idItemCatalogo: number;
  numeroLote: number;
  titulo: string;
  descripcion: string;
  imagenesUrl: string[];
  precioBase: number;
  precioActual: number | null;
}

export interface LoteCatalogoDTO {
  idItemCatalogo: number;
  numeroLote: number;
  titulo: string;
  imagenUrl: string | null;
  precioBase: number;
}

export interface DetalleSubastaDTO {
  subasta: SubastaDetalleInfoDTO;
  itemActual: ItemActualDTO | null;
  catalogo: LoteCatalogoDTO[];
  proximosLotes: LoteCatalogoDTO[];
}
