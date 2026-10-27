package com.acueducto.facturacion.scheduler;

import com.acueducto.facturacion.service.FacturacionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class FacturacionScheduler {
    private final FacturacionService facturacionService;

    public FacturacionScheduler(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @Scheduled(cron = "0 5 0 1 * *", zone = "America/Bogota")
    public void generarFacturacionMensual() {
        facturacionService.generarPeriodo(YearMonth.now());
    }

    @Scheduled(cron = "0 15 0 * * *", zone = "America/Bogota")
    public void actualizarFacturasVencidas() {
        facturacionService.actualizarMorosidad(LocalDate.now());
    }
}
