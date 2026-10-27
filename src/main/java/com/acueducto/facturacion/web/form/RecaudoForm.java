package com.acueducto.facturacion.web.form;

import com.acueducto.facturacion.enums.MedioPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RecaudoForm {
    @NotNull(message = "Seleccione una cuenta")
    private Long cuentaId;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor que cero")
    private BigDecimal valor;

    @NotNull(message = "Seleccione el medio de pago")
    private MedioPago medioPago = MedioPago.EFECTIVO;

    @Size(max = 100, message = "Máximo 100 caracteres")
    private String referencia;

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
