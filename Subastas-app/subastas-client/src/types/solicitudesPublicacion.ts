export type Categoria = "ARTE" | "VEHICULOS" | "JOYAS" | "ROPA" | "OTROS";

export type EstadoSolicitud =
  | "PENDIENTE_REVISION"
  | "INTERES_EMPRESA"
  | "PENDIENTE_ENVIO"
  | "EN_INSPECCION"
  | "PENDIENTE_CONDICIONES_VENTA"
  | "PENDIENTE_POLIZA"
  | "LISTA_PARA_SUBASTA"
  | "DEVOLUCION_PENDIENTE"
  | "DEVUELTA"
  | "RECHAZADA"
  | "CANCELADA";

export type TipoAccionSolicitud =
  | "ACEPTAR_ENVIO_INSPECCION"
  | "ACEPTAR_CONDICIONES_VENTA"
  | "REVISAR_POLIZA"
  | "PAGAR_DEVOLUCION"
  | "COMPROBAR_ORIGEN_LICITO"
  | "PROPUESTA_COLECCION";

export type EstadoAccionSolicitud = "PENDIENTE" | "RESUELTA" | "CANCELADA";

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
  fechaCreacion: string | null;
  cantidadAccionesPendientes: number | null;
  idSubasta: number | null;
  fechaSubasta: string | null;
};

export type AccionSolicitudPublicacion = {
  idAccion: number;
  tipo: TipoAccionSolicitud;
  estado: EstadoAccionSolicitud;
  titulo: string | null;
  descripcion: string | null;
  aceptada: boolean | null;
  comentarioRespuesta: string | null;
  archivoUrl: string | null;
  fechaCreacion: string | null;
  fechaResolucion: string | null;
};

export type PropuestaCondicionesVenta = {
  idPropuesta: number;
  estado: string;
  precioBase: number | null;
  porcentajeComision: number | null;
  idSubasta: number | null;
  tituloSubasta: string | null;
  categoriaMinima: string | null;
  moneda: string | null;
  fechaSubasta: string | null;
  ubicacionSubasta: string | null;
  rematador: string | null;
  motivoRechazoUsuario: string | null;
  fechaCreacion: string | null;
  fechaRespuesta: string | null;
};

export type DevolucionSolicitud = {
  idDevolucion: number;
  estado: string;
  costo: number | null;
  moneda: string | null;
  direccionDestino: string | null;
  idMedioPago: number | null;
  fechaCreacion: string | null;
  fechaPago: string | null;
  fechaEnvio: string | null;
  fechaEntrega: string | null;
};

export type SolicitudPublicacionDetalle = {
  idSolicitud: number;
  titulo: string;
  descripcion: string;
  categoria: Categoria;
  estado: EstadoSolicitud;
  imagenesUrl: string[];
  declaracionPropiedad: boolean;
  direccionDeposito: string | null;
  fechaLimiteEnvio: string | null;
  aceptaDevolucionConCargo: boolean | null;
  fechaRecepcion: string | null;
  ubicacionActual: string | null;
  fechaActualizacionUbicacion: string | null;
  motivoRechazo: string | null;
  accionesPendientes: AccionSolicitudPublicacion[];
  accionesCompletadas: AccionSolicitudPublicacion[];
  propuestaVenta: PropuestaCondicionesVenta | null;
  devolucion: DevolucionSolicitud | null;
  idItem: number | null;
  idSubasta: number | null;
  tituloSubasta: string | null;
  fechaSubasta: string | null;
  ubicacionSubasta: string | null;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
};

export type ResponderAccionRequest = {
  aceptada?: boolean;
  comentario?: string;
  archivoUrl?: string;
};

export type ConfigurarDevolucionRequest = {
  direccionDestino: string;
  idMedioPago: number;
};

export type EstadoPoliza =
  | "PROPUESTA"
  | "ACEPTADA"
  | "RECHAZADA"
  | "AUMENTO_SOLICITADO";

export type PolizaSolicitudResponse = {
  idSolicitud: number;
  idItem: number;
  tituloItem: string;
  idPoliza: number;
  nroPoliza: string;
  montoAsegurado: number;
  premio: number;
  precioBase: number;
  tasaSeguro: number;
  compania: string;
  estado: EstadoPoliza;
};

export type AumentarPolizaRequest = {
  nuevoMontoAsegurado: number;
};
