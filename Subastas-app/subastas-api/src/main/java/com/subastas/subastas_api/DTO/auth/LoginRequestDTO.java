package com.subastas.subastas_api.DTO.auth;

public class LoginRequestDTO {

    private String mail;
    private String password;

    public LoginRequestDTO() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}