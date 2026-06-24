package com.subastas.subastas_api.DTO.auth;

public class UsuarioActualDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String mail;
    private String rol;
    private String estado;
    private String categoria;
    private boolean claveGenerada;
    private boolean requiereMedioDePago;

    public UsuarioActualDTO(Long idUsuario,
                            String nombre,
                            String apellido,
                            String mail,
                            String rol,
                            String estado,
                            String categoria,
                            boolean claveGenerada,
                            boolean requiereMedioDePago) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.rol = rol;
        this.estado = estado;
        this.categoria = categoria;
        this.claveGenerada = claveGenerada;
        this.requiereMedioDePago = requiereMedioDePago;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getMail() {
        return mail;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isClaveGenerada() {
        return claveGenerada;
    }

    public boolean isRequiereMedioDePago() {
        return requiereMedioDePago;
    }
}