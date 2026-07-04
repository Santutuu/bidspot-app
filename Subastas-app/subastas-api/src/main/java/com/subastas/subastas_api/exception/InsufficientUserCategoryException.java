package com.subastas.subastas_api.exception;

import org.springframework.http.HttpStatus;

public class InsufficientUserCategoryException extends BusinessException {
    public InsufficientUserCategoryException() {
        super("La categoria del usuario no permite acceder a esta subasta", HttpStatus.FORBIDDEN);
    }
}
