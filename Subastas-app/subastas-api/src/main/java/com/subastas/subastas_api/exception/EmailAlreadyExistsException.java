package com.subastas.subastas_api.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException(String mail) {
        super("Ya existe un usuario registrado con el mail: " + mail, HttpStatus.CONFLICT);
    }
}