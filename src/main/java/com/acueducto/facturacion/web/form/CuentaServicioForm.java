package com.acueducto.facturacion.web.form;

import com.acueducto.facturacion.enums.EstadoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CuentaServicioForm {
    private Long id;

    // Solo se usa para mostrar el código al editar. El sistema lo genera automáticamente.
    private String codigoCuenta;

    @NotNull(message = "Debe seleccionar un suscriptor")
    private Long suscriptorId;

    @NotNull(message = "Debe seleccionar una tarifa")
    private Long tarifaId;

    @NotBlank(message = "La dirección del servicio es obligatoria")
    @Size(max = 250, message = "Máximo 250 caracteres")
    private String direccionServicio;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoCuenta() { return codigoCuenta; }
    public void setCodigoCuenta(String codigoCuenta) { this.codigoCuenta = codigoCuenta; }
    public Long getSuscriptorId() { return suscriptorId; }
    public void setSuscriptorId(Long suscriptorId) { this.suscriptorId = suscriptorId; }
    public Long getTarifaId() { return tarifaId; }
    public void setTarifaId(Long tarifaId) { this.tarifaId = tarifaId; }
    public String getDireccionServicio() { return direccionServicio; }
    public void setDireccionServicio(String direccionServicio) { this.direccionServicio = direccionServicio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }
}
