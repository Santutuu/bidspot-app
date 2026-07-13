import {
  AccionSolicitudPublicacion,
  SolicitudPublicacionDetalle,
  TipoAccionSolicitud,
} from "@/src/types/solicitudesPublicacion";

type ActionConfig = {
  title: string;
  description: string;
  icon: string;
  cta: string;
};

export const publicationActionConfig: Record<TipoAccionSolicitud, ActionConfig> = {
  ACEPTAR_ENVIO_INSPECCION: {
    title: "Enviar para inspección",
    description: "Confirmá el depósito, la fecha límite y la devolución con cargo si corresponde.",
    icon: "cube-outline",
    cta: "Confirmar envío",
  },
  ACEPTAR_CONDICIONES_VENTA: {
    title: "Condiciones de venta",
    description: "Revisá la subasta, el precio base y la comisión propuesta.",
    icon: "document-text-outline",
    cta: "Responder condiciones",
  },
  REVISAR_POLIZA: {
    title: "Póliza",
    description: "Revisá el seguro del artículo o solicitá un aumento.",
    icon: "shield-checkmark-outline",
    cta: "Revisar póliza",
  },
  PAGAR_DEVOLUCION: {
    title: "Configurar devolución",
    description: "Elegí dirección y medio de pago para recibir el producto.",
    icon: "card-outline",
    cta: "Configurar devolución",
  },
  COMPROBAR_ORIGEN_LICITO: {
    title: "Origen lícito",
    description: "Adjuntá documentación respaldatoria si la empresa la solicita.",
    icon: "document-attach-outline",
    cta: "Enviar comprobante",
  },
  PROPUESTA_COLECCION: {
    title: "Propuesta de colección",
    description: "La empresa propuso agrupar el artículo en una colección.",
    icon: "albums-outline",
    cta: "Responder propuesta",
  },
};

export function getActionConfig(action: AccionSolicitudPublicacion) {
  return publicationActionConfig[action.tipo] ?? {
    title: action.titulo ?? "Acción requerida",
    description: action.descripcion ?? "Hay una acción pendiente para esta publicación.",
    icon: "alert-circle-outline",
    cta: "Responder",
  };
}

export function derivePublicationUIState(solicitud: SolicitudPublicacionDetalle) {
  const hasPendingActions = solicitud.accionesPendientes.length > 0;

  return {
    hasPendingActions,
    showShipment:
      solicitud.estado === "INTERES_EMPRESA" ||
      solicitud.estado === "PENDIENTE_ENVIO" ||
      !!solicitud.direccionDeposito,
    showInspection:
      solicitud.estado === "PENDIENTE_ENVIO" ||
      solicitud.estado === "EN_INSPECCION" ||
      !!solicitud.fechaRecepcion,
    showRejection:
      solicitud.estado === "RECHAZADA" ||
      solicitud.estado === "DEVOLUCION_PENDIENTE" ||
      !!solicitud.motivoRechazo ||
      !!solicitud.devolucion,
    showConditions: !!solicitud.propuestaVenta,
    showAuction: !!solicitud.idSubasta,
    showFinal: solicitud.estado === "LISTA_PARA_SUBASTA",
  };
}
