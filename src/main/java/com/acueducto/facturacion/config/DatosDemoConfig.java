package com.acueducto.facturacion.config;

import com.acueducto.facturacion.entity.*;
import com.acueducto.facturacion.enums.*;
import com.acueducto.facturacion.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DatosDemoConfig {

    @Bean
    @ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
    CommandLineRunner cargarDatosDemo(SuscriptorRepository suscriptorRepository,
                                      TarifaRepository tarifaRepository,
                                      CuentaServicioRepository cuentaRepository,
                                      BeneficioRepository beneficioRepository,
                                      AsignacionBeneficioRepository asignacionRepository) {
        return args -> {
            if (cuentaRepository.count() > 0) return;

            Suscriptor suscriptor = suscriptorRepository.save(
                    new Suscriptor("123456789", "Usuario de prueba"));
            suscriptor.actualizarContacto("3000000000", "demo@example.com", "Dirección de correspondencia");
            suscriptorRepository.save(suscriptor);

            Tarifa tarifa = tarifaRepository.save(
                    new Tarifa("Tarifa residencial", new BigDecimal("35000.00"), LocalDate.of(2026, 1, 1)));

            CuentaServicio cuenta = cuentaRepository.save(
                    new CuentaServicio("AGP-000001", suscriptor, tarifa, "Calle de prueba 123", LocalDate.of(2025, 1, 1)));

            Beneficio descuento = beneficioRepository.save(new Beneficio(
                    "Descuento recurrente 10%",
                    TipoBeneficio.DESCUENTO,
                    ModalidadBeneficio.RECURRENTE,
                    TipoCalculoBeneficio.PORCENTAJE,
                    new BigDecimal("10.00"),
                    LocalDate.of(2026, 1, 1),
                    null
            ));

            Beneficio subsidio = beneficioRepository.save(new Beneficio(
                    "Subsidio temporal",
                    TipoBeneficio.SUBSIDIO,
                    ModalidadBeneficio.TEMPORAL,
                    TipoCalculoBeneficio.VALOR_FIJO,
                    new BigDecimal("5000.00"),
                    LocalDate.of(2026, 8, 1),
                    LocalDate.of(2026, 12, 31)
            ));

            asignacionRepository.save(new AsignacionBeneficio(cuenta, descuento));
            asignacionRepository.save(new AsignacionBeneficio(cuenta, subsidio));
        };
    }
}
