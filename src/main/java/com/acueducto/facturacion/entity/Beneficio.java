package com.acueducto.facturacion.entity;

import com.acueducto.facturacion.enums.ModalidadBeneficio;
import com.acueducto.facturacion.enums.TipoBeneficio;
import com.acueducto.facturacion.enums.TipoCalculoBeneficio;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "beneficios")
public class Beneficio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBeneficio tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadBeneficio modalidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCalculoBeneficio tipoCalculo;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;

    @Column(nullable = false)
    private boolean activo = true;

    protected Beneficio() {}

    public Beneficio(String nombre, TipoBeneficio tipo, ModalidadBeneficio modalidad,
                     TipoCalculoBeneficio tipoCalculo, BigDecimal valor, LocalDate vigenteDesde,
                     LocalDate vigenteHasta) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.tipoCalculo = tipoCalculo;
        this.valor = valor;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public TipoBeneficio getTipo() { return tipo; }
    public ModalidadBeneficio getModalidad() { return modalidad; }
    public TipoCalculoBeneficio getTipoCalculo() { return tipoCalculo; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public boolean isActivo() { return activo; }

    public void actualizar(String nombre, TipoBeneficio tipo, ModalidadBeneficio modalidad,
                           TipoCalculoBeneficio tipoCalculo, BigDecimal valor,
                           LocalDate vigenteDesde, LocalDate vigenteHasta, boolean activo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.tipoCalculo = tipoCalculo;
        this.valor = valor;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
        this.activo = activo;
    }

    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean estaVigente(LocalDate fecha) {
        boolean despuesInicio = !fecha.isBefore(vigenteDesde);
        boolean antesFin = vigenteHasta == null || !fecha.isAfter(vigenteHasta);
        return activo && despuesInicio && antesFin;
    }
}
