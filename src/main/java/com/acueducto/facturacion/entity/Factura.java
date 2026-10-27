package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.EstadoFactura;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "facturas",
       uniqueConstraints = @UniqueConstraint(name = "uk_factura_cuenta_periodo", columnNames = {"cuenta_servicio_id", "periodo_anio", "periodo_mes"}))
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String numeroFactura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_servicio_id")
    private CuentaServicio cuentaServicio;

    @Column(name = "periodo_anio", nullable = false)
    private int periodoAnio;

    @Column(name = "periodo_mes", nullable = false)
    private int periodoMes;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDescuentos = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalSubsidios = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    protected Factura() {}

    public Factura(String numeroFactura, CuentaServicio cuentaServicio, int periodoAnio, int periodoMes,
                   LocalDate fechaEmision, LocalDate fechaVencimiento, BigDecimal subtotal) {
        this.numeroFactura = numeroFactura;
        this.cuentaServicio = cuentaServicio;
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.subtotal = subtotal;
        this.total = subtotal;
        this.saldo = subtotal;
    }

    public Long getId() { return id; }
    public String getNumeroFactura() { return numeroFactura; }
    public CuentaServicio getCuentaServicio() { return cuentaServicio; }
    public int getPeriodoAnio() { return periodoAnio; }
    public int getPeriodoMes() { return periodoMes; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTotalDescuentos() { return totalDescuentos; }
    public BigDecimal getTotalSubsidios() { return totalSubsidios; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getSaldo() { return saldo; }
    public EstadoFactura getEstado() { return estado; }

    public void aplicarBeneficio(BigDecimal valor, boolean esDescuento) {
        if (valor.signum() <= 0) return;
        BigDecimal aplicado = valor.min(saldo);
        if (esDescuento) {
            totalDescuentos = totalDescuentos.add(aplicado);
        } else {
            totalSubsidios = totalSubsidios.add(aplicado);
        }
        total = total.subtract(aplicado).max(BigDecimal.ZERO);
        saldo = saldo.subtract(aplicado).max(BigDecimal.ZERO);
        recalcularEstado();
    }

    public void aplicarPago(BigDecimal valor) {
        if (valor.signum() <= 0) return;
        saldo = saldo.subtract(valor).max(BigDecimal.ZERO);
        recalcularEstado();
    }

    public void marcarVencidaSiCorresponde(LocalDate hoy) {
        if (saldo.signum() > 0 && hoy.isAfter(fechaVencimiento) && estado != EstadoFactura.ANULADA) {
            estado = EstadoFactura.VENCIDA;
        }
    }

    public void anular() {
        estado = EstadoFactura.ANULADA;
    }

    private void recalcularEstado() {
        if (estado == EstadoFactura.ANULADA) return;
        boolean estabaVencida = estado == EstadoFactura.VENCIDA;
        if (saldo.signum() == 0) {
            estado = EstadoFactura.PAGADA;
        } else if (estabaVencida) {
            estado = EstadoFactura.VENCIDA;
        } else if (saldo.compareTo(total) < 0) {
            estado = EstadoFactura.PARCIAL;
        } else {
            estado = EstadoFactura.PENDIENTE;
        }
    }
}
