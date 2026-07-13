package com.subastas.subastas_api.model;

public enum TipoAccionSolicitud {

    /*
     * El usuario debe aceptar:
     * - enviar el producto;
     * - la dirección del depósito;
     * - la eventual devolución con cargo.
     */
    ACEPTAR_ENVIO_INSPECCION,

    /*
     * El usuario debe aceptar o rechazar:
     * - subasta propuesta;
     * - precio base;
     * - comisión;
     * - moneda;
     * - fecha, hora y lugar.
     */
    ACEPTAR_CONDICIONES_VENTA,

    /*
     * El usuario revisa la póliza propuesta,
     * acepta el monto asegurado o solicita uno superior.
     */
    REVISAR_POLIZA,

    /*
     * El usuario configura y paga la devolución.
     */
    PAGAR_DEVOLUCION,

    /*
     * Acción opcional que podrá solicitar la empresa.
     */
    COMPROBAR_ORIGEN_LICITO,

    /*
     * Acción opcional para una futura propuesta
     * de subasta como colección.
     */
    PROPUESTA_COLECCION
}