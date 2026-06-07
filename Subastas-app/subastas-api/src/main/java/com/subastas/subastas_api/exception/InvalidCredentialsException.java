package com.subastas.subastas_api.exception;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("Email o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
    }
}
