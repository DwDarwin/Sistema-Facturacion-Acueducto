package com.acueducto.facturacion.web.view;

import com.acueducto.facturacion.entity.Factura;
import com.acueducto.facturacion.entity.FacturaBeneficio;

import java.math.BigDecimal;
import java.util.List;

public record FacturaImpresionView(
        Factura factura,
        List<FacturaBeneficio> beneficios,
        BigDecimal carteraAnterior,
        BigDecimal totalAPagar
) {}
