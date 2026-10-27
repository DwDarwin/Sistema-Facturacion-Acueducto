package com.acueducto.facturacion.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SuscriptorForm {
    private Long id;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String documento;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 160, message = "Máximo 160 caracteres")
    private String nombreCompleto;

    @Size(max = 30, message = "Máximo 30 caracteres")
    private String telefono;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 160, message = "Máximo 160 caracteres")
    private String email;

    @Size(max = 250, message = "Máximo 250 caracteres")
    private String direccionCorrespondencia;

    private boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccionCorrespondencia() { return direccionCorrespondencia; }
    public void setDireccionCorrespondencia(String direccionCorrespondencia) { this.direccionCorrespondencia = direccionCorrespondencia; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
