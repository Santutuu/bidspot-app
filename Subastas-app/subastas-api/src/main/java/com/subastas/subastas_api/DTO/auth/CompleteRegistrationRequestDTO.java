package com.subastas.subastas_api.DTO.auth;

public class CompleteRegistrationRequestDTO {

    private String mail;
    private String password;
    private String confirmPassword;

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}