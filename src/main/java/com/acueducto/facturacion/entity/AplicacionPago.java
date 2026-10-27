package com.acueducto.facturacion.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "aplicaciones_pago")
public class AplicacionPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Factura factura;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valorAplicado;

    protected AplicacionPago() {}

    public AplicacionPago(Pago pago, Factura factura, BigDecimal valorAplicado) {
        this.pago = pago;
        this.factura = factura;
        this.valorAplicado = valorAplicado;
    }

    public Long getId() { return id; }
    public Pago getPago() { return pago; }
    public Factura getFactura() { return factura; }
    public BigDecimal getValorAplicado() { return valorAplicado; }
}
