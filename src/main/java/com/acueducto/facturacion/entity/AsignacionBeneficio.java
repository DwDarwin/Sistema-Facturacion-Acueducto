package com.acueducto.facturacion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones_beneficio",
       uniqueConstraints = @UniqueConstraint(name = "uk_cuenta_beneficio", columnNames = {"cuenta_servicio_id", "beneficio_id"}))
public class AsignacionBeneficio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_servicio_id")
    private CuentaServicio cuentaServicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficio_id")
    private Beneficio beneficio;

    @Column(nullable = false)
    private boolean activa = true;

    protected AsignacionBeneficio() {}

    public AsignacionBeneficio(CuentaServicio cuentaServicio, Beneficio beneficio) {
        this.cuentaServicio = cuentaServicio;
        this.beneficio = beneficio;
    }

    public Long getId() { return id; }
    public CuentaServicio getCuentaServicio() { return cuentaServicio; }
    public Beneficio getBeneficio() { return beneficio; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
