package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.EstadoCuenta;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cuentas_servicio", uniqueConstraints = @UniqueConstraint(name = "uk_cuenta_codigo", columnNames = "codigo_cuenta"))
public class CuentaServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_cuenta", nullable = false, length = 40)
    private String codigoCuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Suscriptor suscriptor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Tarifa tarifa;

    @Column(nullable = false, length = 250)
    private String direccionServicio;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    protected CuentaServicio() {}

    public CuentaServicio(String codigoCuenta, Suscriptor suscriptor, Tarifa tarifa,
                          String direccionServicio, LocalDate fechaInicio) {
        this.codigoCuenta = codigoCuenta;
        this.suscriptor = suscriptor;
        this.tarifa = tarifa;
        this.direccionServicio = direccionServicio;
        this.fechaInicio = fechaInicio;
    }

    public Long getId() { return id; }
    public String getCodigoCuenta() { return codigoCuenta; }
    public Suscriptor getSuscriptor() { return suscriptor; }
    public Tarifa getTarifa() { return tarifa; }
    public String getDireccionServicio() { return direccionServicio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public EstadoCuenta getEstado() { return estado; }

    public void actualizar(Suscriptor suscriptor, Tarifa tarifa,
                           String direccionServicio, LocalDate fechaInicio, EstadoCuenta estado) {
        this.suscriptor = suscriptor;
        this.tarifa = tarifa;
        this.direccionServicio = direccionServicio;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    public void asignarCodigoCuenta(String codigoCuenta) { this.codigoCuenta = codigoCuenta; }
    public void cambiarTarifa(Tarifa tarifa) { this.tarifa = tarifa; }
    public void cambiarEstado(EstadoCuenta estado) { this.estado = estado; }
}
