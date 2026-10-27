package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.OrigenBeneficio;
import com.acueducto.facturacion.enums.TipoBeneficio;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factura_beneficios")
public class FacturaBeneficio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Factura factura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBeneficio tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigenBeneficio origen;

    @Column(nullable = false, length = 180)
    private String concepto;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valorAplicado;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    protected FacturaBeneficio() {}

    public FacturaBeneficio(Factura factura, TipoBeneficio tipo, OrigenBeneficio origen,
                            String concepto, BigDecimal valorAplicado) {
        this.factura = factura;
        this.tipo = tipo;
        this.origen = origen;
        this.concepto = concepto;
        this.valorAplicado = valorAplicado;
    }

    public Long getId() { return id; }
    public Factura getFactura() { return factura; }
    public TipoBeneficio getTipo() { return tipo; }
    public OrigenBeneficio getOrigen() { return origen; }
    public String getConcepto() { return concepto; }
    public BigDecimal getValorAplicado() { return valorAplicado; }
}
