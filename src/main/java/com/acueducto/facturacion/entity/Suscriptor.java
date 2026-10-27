package com.acueducto.facturacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "suscriptores", uniqueConstraints = @UniqueConstraint(name = "uk_suscriptor_documento", columnNames = "documento"))
public class Suscriptor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String documento;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String nombreCompleto;

    @Column(length = 30)
    private String telefono;

    @Column(length = 160)
    private String email;

    @Column(length = 250)
    private String direccionCorrespondencia;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    protected Suscriptor() {}

    public Suscriptor(String documento, String nombreCompleto) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getId() { return id; }
    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getDireccionCorrespondencia() { return direccionCorrespondencia; }
    public boolean isActivo() { return activo; }

    public void actualizarDatos(String documento, String nombreCompleto, String telefono,
                                String email, String direccionCorrespondencia, boolean activo) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccionCorrespondencia = direccionCorrespondencia;
        this.activo = activo;
    }

    public void actualizarContacto(String telefono, String email, String direccionCorrespondencia) {
        this.telefono = telefono;
        this.email = email;
        this.direccionCorrespondencia = direccionCorrespondencia;
    }

    public void setActivo(boolean activo) { this.activo = activo; }
}
