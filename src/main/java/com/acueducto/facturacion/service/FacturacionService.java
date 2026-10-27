package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.*;
import com.acueducto.facturacion.enums.*;
import com.acueducto.facturacion.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class FacturacionService {

    private final CuentaServicioRepository cuentaRepository;
    private final FacturaRepository facturaRepository;
    private final AsignacionBeneficioRepository asignacionRepository;
    private final FacturaBeneficioRepository facturaBeneficioRepository;
    private final int diaVencimiento;

    public FacturacionService(CuentaServicioRepository cuentaRepository,
                              FacturaRepository facturaRepository,
                              AsignacionBeneficioRepository asignacionRepository,
                              FacturaBeneficioRepository facturaBeneficioRepository,
                              @Value("${app.facturacion.dia-vencimiento:15}") int diaVencimiento) {
        this.cuentaRepository = cuentaRepository;
        this.facturaRepository = facturaRepository;
        this.asignacionRepository = asignacionRepository;
        this.facturaBeneficioRepository = facturaBeneficioRepository;
        this.diaVencimiento = diaVencimiento;
    }

    @Transactional
    public int generarPeriodo(YearMonth periodo) {
        List<CuentaServicio> cuentas = cuentaRepository.findByEstado(EstadoCuenta.ACTIVA);
        int generadas = 0;

        for (CuentaServicio cuenta : cuentas) {
            if (facturaRepository.existsByCuentaServicioIdAndPeriodoAnioAndPeriodoMes(
                    cuenta.getId(), periodo.getYear(), periodo.getMonthValue())) {
                continue;
            }

            LocalDate fechaEmision = periodo.atDay(1);
            Tarifa tarifa = cuenta.getTarifa();
            if (!tarifa.estaVigente(fechaEmision)) {
                continue;
            }

            int dia = Math.min(diaVencimiento, periodo.lengthOfMonth());
            LocalDate vencimiento = periodo.atDay(dia);
            BigDecimal subtotal = tarifa.getValorMensual().setScale(2, RoundingMode.HALF_UP);

            String numero = "FAC-%d%02d-%08d".formatted(
                    periodo.getYear(), periodo.getMonthValue(), cuenta.getId());

            Factura factura = new Factura(
                    numero,
                    cuenta,
                    periodo.getYear(),
                    periodo.getMonthValue(),
                    fechaEmision,
                    vencimiento,
                    subtotal
            );
            facturaRepository.save(factura);

            aplicarBeneficiosAutomaticos(factura, fechaEmision);
            generadas++;
        }

        return generadas;
    }

    private void aplicarBeneficiosAutomaticos(Factura factura, LocalDate fecha) {
        List<AsignacionBeneficio> asignaciones =
                asignacionRepository.findByCuentaServicioIdAndActivaTrue(factura.getCuentaServicio().getId());

        for (AsignacionBeneficio asignacion : asignaciones) {
            Beneficio beneficio = asignacion.getBeneficio();
            if (!beneficio.estaVigente(fecha)) {
                continue;
            }

            BigDecimal calculado = calcularBeneficio(beneficio, factura.getSubtotal());
            BigDecimal aplicado = calculado.min(factura.getSaldo()).max(BigDecimal.ZERO);
            if (aplicado.signum() == 0) {
                continue;
            }

            boolean descuento = beneficio.getTipo() == TipoBeneficio.DESCUENTO;
            factura.aplicarBeneficio(aplicado, descuento);

            facturaBeneficioRepository.save(new FacturaBeneficio(
                    factura,
                    beneficio.getTipo(),
                    OrigenBeneficio.REGLA,
                    beneficio.getNombre(),
                    aplicado
            ));
        }
    }

    private BigDecimal calcularBeneficio(Beneficio beneficio, BigDecimal base) {
        if (beneficio.getTipoCalculo() == TipoCalculoBeneficio.PORCENTAJE) {
            return base
                    .multiply(beneficio.getValor())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return beneficio.getValor().setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public Factura aplicarBeneficioOcasional(Long facturaId,
                                              TipoBeneficio tipo,
                                              String concepto,
                                              BigDecimal valorSolicitado) {
        if (valorSolicitado == null || valorSolicitado.signum() <= 0) {
            throw new IllegalArgumentException("El valor debe ser mayor que cero");
        }
        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo del beneficio ocasional");
        }

        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new IllegalStateException("No se puede modificar una factura anulada");
        }

        BigDecimal aplicado = valorSolicitado.setScale(2, RoundingMode.HALF_UP)
                .min(factura.getSaldo());

        if (aplicado.signum() <= 0) {
            throw new IllegalStateException("La factura no tiene saldo al cual aplicar el beneficio");
        }

        factura.aplicarBeneficio(aplicado, tipo == TipoBeneficio.DESCUENTO);
        facturaBeneficioRepository.save(new FacturaBeneficio(
                factura,
                tipo,
                OrigenBeneficio.OCASIONAL,
                concepto.trim(),
                aplicado
        ));

        return factura;
    }

    @Transactional(readOnly = true)
    public BigDecimal carteraAnterior(Factura factura) {
        BigDecimal valor = facturaRepository.calcularCarteraAnterior(
                factura.getCuentaServicio().getId(), factura.getId(), factura.getFechaEmision());
        return valor == null ? BigDecimal.ZERO : valor;
    }

    @Transactional
    public int actualizarMorosidad(LocalDate hoy) {
        List<Factura> vencidas = facturaRepository.findBySaldoGreaterThanAndFechaVencimientoBeforeAndEstadoNot(
                BigDecimal.ZERO, hoy, EstadoFactura.ANULADA);
        vencidas.forEach(f -> f.marcarVencidaSiCorresponde(hoy));
        return vencidas.size();
    }
}
