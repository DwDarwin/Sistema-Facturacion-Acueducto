package com.acueducto.facturacion.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TarifaForm {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;

    @NotNull(message = "El valor mensual es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor que cero")
    private BigDecimal valorMensual;

    @NotNull(message = "La fecha inicial es obligatoria")
    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;
    private boolean activa = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getValorMensual() { return valorMensual; }
    public void setValorMensual(BigDecimal valorMensual) { this.valorMensual = valorMensual; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
