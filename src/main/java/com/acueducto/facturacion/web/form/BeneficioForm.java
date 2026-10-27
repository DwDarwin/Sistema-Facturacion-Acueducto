package com.acueducto.facturacion.web.form;

import com.acueducto.facturacion.enums.ModalidadBeneficio;
import com.acueducto.facturacion.enums.TipoBeneficio;
import com.acueducto.facturacion.enums.TipoCalculoBeneficio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BeneficioForm {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "Máximo 140 caracteres")
    private String nombre;

    @NotNull(message = "Seleccione si es descuento o subsidio")
    private TipoBeneficio tipo;

    @NotNull(message = "Seleccione la modalidad")
    private ModalidadBeneficio modalidad;

    @NotNull(message = "Seleccione cómo se calcula")
    private TipoCalculoBeneficio tipoCalculo;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor que cero")
    private BigDecimal valor;

    @NotNull(message = "La fecha inicial es obligatoria")
    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;
    private boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoBeneficio getTipo() { return tipo; }
    public void setTipo(TipoBeneficio tipo) { this.tipo = tipo; }
    public ModalidadBeneficio getModalidad() { return modalidad; }
    public void setModalidad(ModalidadBeneficio modalidad) { this.modalidad = modalidad; }
    public TipoCalculoBeneficio getTipoCalculo() { return tipoCalculo; }
    public void setTipoCalculo(TipoCalculoBeneficio tipoCalculo) { this.tipoCalculo = tipoCalculo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
