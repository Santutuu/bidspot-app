package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "seguros")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poliza")
    private Long idPoliza;

    @Column(
            name = "nropoliza",
            nullable = false,
            unique = true,
            length = 30
    )
    private String nroPoliza;

    @Column(
            name = "compania",
            nullable = false,
            length = 150
    )
    private String compania;

    @Column(name = "polizacombinada")
    private String polizaCombinada = "no";

    /*
     * La columna legacy seguros.importe es NUMERIC.
     */
    @Column(
            name = "importe",
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal premio;

    @Column(name = "montoasegurado")
    private Float montoAsegurado;

    @Column(name = "preciobase")
    private Float precioBase;

    @Column(name = "tasaseguro")
    private Float tasaSeguro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPoliza estado =
            EstadoPoliza.PROPUESTA;

    public Poliza() {
    }

    public Poliza(
            String nroPoliza,
            Float montoAsegurado,
            Float premio,
            Float precioBase,
            Float tasaSeguro,
            String compania
    ) {
        this.nroPoliza =
                normalizarTexto(
                        nroPoliza,
                        "El número de póliza es obligatorio"
                );

        this.compania =
                normalizarTexto(
                        compania,
                        "La compañía es obligatoria"
                );

        validarValorPositivo(
                montoAsegurado,
                "El monto asegurado debe ser mayor a cero"
        );

        validarValorPositivo(
                premio,
                "El premio debe ser mayor a cero"
        );

        validarValorPositivo(
                precioBase,
                "El precio base debe ser mayor a cero"
        );

        validarTasa(tasaSeguro);

        this.montoAsegurado =
                montoAsegurado;

        this.premio =
                convertirDecimal(premio);

        this.precioBase =
                precioBase;

        this.tasaSeguro =
                tasaSeguro;

        this.polizaCombinada = "no";
        this.estado = EstadoPoliza.PROPUESTA;
    }

    public void aceptar() {
        if (estado == EstadoPoliza.RECHAZADA) {
            throw new IllegalStateException(
                    "No se puede aceptar una póliza rechazada"
            );
        }

        this.estado = EstadoPoliza.ACEPTADA;
    }

    public void rechazar() {
        if (estado == EstadoPoliza.ACEPTADA) {
            throw new IllegalStateException(
                    "No se puede rechazar una póliza aceptada"
            );
        }

        this.estado = EstadoPoliza.RECHAZADA;
    }

    public void solicitarAumento(
            Float nuevoMontoAsegurado
    ) {
        validarValorPositivo(
                nuevoMontoAsegurado,
                "El monto asegurado debe ser mayor a cero"
        );

        if (montoAsegurado != null
                && nuevoMontoAsegurado <= montoAsegurado) {

            throw new IllegalArgumentException(
                    "El nuevo monto asegurado debe ser mayor al actual"
            );
        }

        if (estado == EstadoPoliza.ACEPTADA) {
            throw new IllegalStateException(
                    "No se puede modificar una póliza aceptada"
            );
        }

        if (estado == EstadoPoliza.RECHAZADA) {
            throw new IllegalStateException(
                    "No se puede modificar una póliza rechazada"
            );
        }

        this.montoAsegurado =
                nuevoMontoAsegurado;

        this.premio =
                convertirDecimal(
                        calcularPremio(
                                nuevoMontoAsegurado,
                                tasaSeguro
                        )
                );

        this.estado =
                EstadoPoliza.AUMENTO_SOLICITADO;
    }

    public static Float calcularPremio(
            Float montoAsegurado,
            Float tasaSeguro
    ) {
        if (montoAsegurado == null
                || tasaSeguro == null) {

            return null;
        }

        return montoAsegurado
                * tasaSeguro
                / 100f;
    }

    private static BigDecimal convertirDecimal(
            Float valor
    ) {
        if (valor == null) {
            return null;
        }

        return BigDecimal
                .valueOf(valor.doubleValue())
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private static void validarValorPositivo(
            Float valor,
            String mensaje
    ) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }

    private static void validarTasa(
            Float tasaSeguro
    ) {
        if (tasaSeguro == null
                || tasaSeguro <= 0
                || tasaSeguro > 100) {

            throw new IllegalArgumentException(
                    "La tasa debe ser mayor a cero y no superar el 100 %"
            );
        }
    }

    private static String normalizarTexto(
            String valor,
            String mensaje
    ) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    mensaje
            );
        }

        return valor.trim();
    }

    public Long getIdPoliza() {
        return idPoliza;
    }

    public String getNroPoliza() {
        return nroPoliza;
    }

    public Float getMontoAsegurado() {
        return montoAsegurado;
    }

    /*
     * Se conserva Float para no romper los DTO y services actuales.
     */
    public Float getPremio() {
        return premio == null
                ? null
                : premio.floatValue();
    }

    public BigDecimal getPremioDecimal() {
        return premio;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getTasaSeguro() {
        return tasaSeguro;
    }

    public String getCompania() {
        return compania;
    }

    public String getPolizaCombinada() {
        return polizaCombinada;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }
}