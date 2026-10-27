package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.TipoAjuste;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustes")
public class Ajuste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Factura factura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoAjuste tipo;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 300)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    protected Ajuste() {}

    public Ajuste(Factura factura, TipoAjuste tipo, BigDecimal valor, String motivo) {
        this.factura = factura;
        this.tipo = tipo;
        this.valor = valor;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public Factura getFactura() { return factura; }
    public TipoAjuste getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public String getMotivo() { return motivo; }
}
