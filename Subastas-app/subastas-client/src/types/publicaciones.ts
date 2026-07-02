export type EstadoPublicacion =
  | "PENDIENTE"
  | "EN_REVISION"
  | "ACEPTADA"
  | "RECHAZADA"
  | "CANCELADA"
  | "VENDIDA";

export type TipoAccionRequerida =
  | "ENVIAR_ITEM"
  | "COMPROBAR_ORIGEN_LICITO"
  | "PROPUESTA_COLECCION"
  | "ACEPTAR_POLIZA"
  | "MODIFICAR_POLIZA"
  | "ACEPTAR_CONDICIONES_VENTA";

export type AccionRequeridaMock = {
  id: string;
  tipo: TipoAccionRequerida;
  titulo: string;
  descripcion: string;
  estado: "PENDIENTE" | "RESPONDIDA";
};

export type PublicacionMock = {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: string;
  estado: EstadoPublicacion;
  explicacionEstado: string;
  imagenes: number[];
  acciones: AccionRequeridaMock[];
  precioInicial?: number;
  precioFinal?: number;
  motivoRechazo?: string;
  ubicacionDeposito?: string;
  fechaRecepcion?: string;
  subasta?: {
    titulo: string;
    fecha: string;
    hora: string;
    lugar: string;
    valorBase: number;
    comision: string;
  };
  poliza?: {
    empresa: string;
    numero: string;
    cobertura: number;
    prima: number;
    estado: string;
  };
};
