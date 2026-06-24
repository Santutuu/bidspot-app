package com.subastas.subastas_api.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarSolicitudRecibida(String mail, String nombre) {
        System.out.println("=================================");
        System.out.println("MAIL SIMULADO");
        System.out.println("Para: " + mail);
        System.out.println("Asunto: Solicitud de registro recibida");
        System.out.println("Hola " + nombre + ", recibimos tu solicitud.");
        System.out.println("La empresa revisará tus datos.");
        System.out.println("=================================");
    }

    public void enviarCuentaValidada(String mail, String nombre) {
        System.out.println("=================================");
        System.out.println("MAIL SIMULADO");
        System.out.println("Para: " + mail);
        System.out.println("Asunto: Cuenta validada");
        System.out.println("Hola " + nombre + ", tu cuenta fue validada.");
        System.out.println("Ingresá a la app para generar tu clave personal.");
        System.out.println("=================================");
    }
}