package com.acueducto.facturacion.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tarifas")
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valorMensual;

    @Column(nullable = false)
    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;

    @Column(nullable = false)
    private boolean activa = true;

    protected Tarifa() {}

    public Tarifa(String nombre, BigDecimal valorMensual, LocalDate vigenteDesde) {
        this.nombre = nombre;
        this.valorMensual = valorMensual;
        this.vigenteDesde = vigenteDesde;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getValorMensual() { return valorMensual; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public boolean isActiva() { return activa; }

    public boolean estaVigente(LocalDate fecha) {
        boolean despuesInicio = !fecha.isBefore(vigenteDesde);
        boolean antesFin = vigenteHasta == null || !fecha.isAfter(vigenteHasta);
        return activa && despuesInicio && antesFin;
    }

    public void actualizar(String nombre, BigDecimal valorMensual, LocalDate vigenteDesde,
                           LocalDate vigenteHasta, boolean activa) {
        this.nombre = nombre;
        this.valorMensual = valorMensual;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
        this.activa = activa;
    }

    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
