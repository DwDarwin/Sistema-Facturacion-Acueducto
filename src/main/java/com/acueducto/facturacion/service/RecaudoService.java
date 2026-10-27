package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.*;
import com.acueducto.facturacion.enums.EstadoFactura;
import com.acueducto.facturacion.enums.MedioPago;
import com.acueducto.facturacion.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RecaudoService {
    private final CuentaServicioRepository cuentaRepository;
    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final AplicacionPagoRepository aplicacionRepository;

    public RecaudoService(CuentaServicioRepository cuentaRepository,
                          FacturaRepository facturaRepository,
                          PagoRepository pagoRepository,
                          AplicacionPagoRepository aplicacionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.aplicacionRepository = aplicacionRepository;
    }

    @Transactional
    public Pago registrarPago(Long cuentaId, BigDecimal valor, MedioPago medioPago, String referencia) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("El valor del pago debe ser mayor que cero");
        }

        CuentaServicio cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de servicio no encontrada"));

        List<Factura> pendientes = facturaRepository
                .findByCuentaServicioIdAndSaldoGreaterThanOrderByFechaEmisionAsc(cuentaId, BigDecimal.ZERO)
                .stream()
                .filter(f -> f.getEstado() != EstadoFactura.ANULADA)
                .toList();

        BigDecimal deuda = pendientes.stream()
                .map(Factura::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (deuda.signum() <= 0) {
            throw new IllegalStateException("La cuenta no tiene saldo pendiente");
        }

        BigDecimal valorPago = valor.setScale(2, RoundingMode.HALF_UP);
        if (valorPago.compareTo(deuda) > 0) {
            throw new IllegalArgumentException("El pago supera la deuda actual de la cuenta");
        }

        Pago pago = pagoRepository.save(new Pago(
                cuenta,
                valorPago,
                medioPago,
                referencia == null || referencia.isBlank() ? null : referencia.trim()
        ));

        for (Factura factura : pendientes) {
            if (pago.getValorNoAplicado().signum() <= 0) break;
            BigDecimal aplicado = pago.getValorNoAplicado().min(factura.getSaldo());
            factura.aplicarPago(aplicado);
            pago.aplicar(aplicado);
            aplicacionRepository.save(new AplicacionPago(pago, factura, aplicado));
        }

        return pago;
    }
}
