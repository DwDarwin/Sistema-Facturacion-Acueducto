package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.MedioPago;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CuentaServicio cuentaServicio;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valorNoAplicado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private MedioPago medioPago;

    @Column(length = 100)
    private String referencia;

    @Column(nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    protected Pago() {}

    public Pago(CuentaServicio cuentaServicio, BigDecimal valor, MedioPago medioPago, String referencia) {
        this.cuentaServicio = cuentaServicio;
        this.valor = valor;
        this.valorNoAplicado = valor;
        this.medioPago = medioPago;
        this.referencia = referencia;
    }

    public Long getId() { return id; }
    public CuentaServicio getCuentaServicio() { return cuentaServicio; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getValorNoAplicado() { return valorNoAplicado; }
    public MedioPago getMedioPago() { return medioPago; }
    public String getReferencia() { return referencia; }
    public LocalDateTime getFechaPago() { return fechaPago; }

    public void aplicar(BigDecimal valorAplicado) {
        this.valorNoAplicado = this.valorNoAplicado.subtract(valorAplicado).max(BigDecimal.ZERO);
    }
}
