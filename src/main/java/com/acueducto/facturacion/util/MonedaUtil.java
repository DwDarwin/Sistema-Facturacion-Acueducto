package com.acueducto.facturacion.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Component("moneda")
public class MonedaUtil {
    private static final Locale LOCALE_CO = Locale.forLanguageTag("es-CO");
    private static final Currency COP = Currency.getInstance("COP");

    public String formatear(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor;
        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE_CO);
        formato.setCurrency(COP);
        formato.setMinimumFractionDigits(0);
        formato.setMaximumFractionDigits(2);
        return formato.format(seguro);
    }
}
