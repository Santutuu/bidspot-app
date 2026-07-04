export type Categoria = "ARTE" | "VEHICULOS" | "JOYAS" | "ROPA" | "OTROS";

export type EstadoSolicitud =
  | "PENDIENTE"
  | "EN_REVISION"
  | "ACEPTADA"
  | "RECHAZADA"
  | "CANCELADA";

export type AccionRequerida =
  | "ENVIAR_ITEM"
  | "PROPUESTA_COLECCION"
  | "ACEPTAR_CONDICIONES_VENTA"
  | "ACEPTAR_POLIZA"
  | "MODIFICAR_POLIZA"
  | "COMPROBAR_ORIGEN_LICITO";

export type TipoRespuestaAccion =
  | "COMENTARIO"
  | "ARCHIVO"
  | "ACEPTACION"
  | "RECHAZO"
  | "MONTO_ASEGURADO";

export type SolicitudPublicacionRequest = {
  categoria: Categoria;
  titulo: string;
  descripcion: string;
  imagenesUrl: string[];
  declaracionPropiedad: boolean;
};

export type SolicitudPublicacionResumen = {
  idSolicitud: number;
  titulo: string;
  estado: EstadoSolicitud;
  categoria: Categoria;
  imagenUrl: string | null;
  idSubasta: number | null;
  fechaSubasta: string | null;
};

export type RespuestaAccion = {
  idRespuesta: number;
  accion: AccionRequerida;
  tipoRespuesta: TipoRespuestaAccion;
  aceptada: boolean | null;
  comentario: string | null;
  archivoUrl: string | null;
  montoAseguradoSolicitado: number | null;
  fechaRespuesta: string;
};

export type SolicitudPublicacionDetalle = {
  idSolicitud: number;
  titulo: string;
  descripcion: string;
  categoria: Categoria;
  estado: EstadoSolicitud;
  imagenesUrl: string[];
  declaracionPropiedad: boolean;
  accionesRequeridas: AccionRequerida[];
  respuestasAcciones: RespuestaAccion[];
  motivoRechazo: string | null;
  ubicacionDeposito: string | null;
  idSubasta: number | null;
  tituloSubasta: string | null;
  fechaSubasta: string | null;
  ubicacionSubasta: string | null;
};

export type ResponderAccionRequest = {
  tipoRespuesta: TipoRespuestaAccion;
  aceptada?: boolean;
  comentario?: string;
  archivoUrl?: string;
  montoAseguradoSolicitado?: number;
};
