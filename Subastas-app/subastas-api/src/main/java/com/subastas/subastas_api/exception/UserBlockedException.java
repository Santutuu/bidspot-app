package com.subastas.subastas_api.exception;

import org.springframework.http.HttpStatus;

public class UserBlockedException extends BusinessException {

    public UserBlockedException() {
        super("La cuenta se encuentra bloqueada", HttpStatus.FORBIDDEN);
    }
}