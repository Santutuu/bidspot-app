package com.subastas.subastas_api.model;

/**
 * Estado del proceso de validación comercial de una persona.
 *
 * No representa si la cuenta está técnicamente bloqueada.
 */
public enum EstadoRegistro {

    PENDIENTE_VALIDACION,
    VALIDADO,
    RECHAZADO
}