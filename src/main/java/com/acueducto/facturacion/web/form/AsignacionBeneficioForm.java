package com.acueducto.facturacion.web.form;

import jakarta.validation.constraints.NotNull;

public class AsignacionBeneficioForm {
    @NotNull(message = "Seleccione una cuenta")
    private Long cuentaId;

    @NotNull(message = "Seleccione un beneficio")
    private Long beneficioId;

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public Long getBeneficioId() { return beneficioId; }
    public void setBeneficioId(Long beneficioId) { this.beneficioId = beneficioId; }
}
