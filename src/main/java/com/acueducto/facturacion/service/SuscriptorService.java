package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.Suscriptor;
import com.acueducto.facturacion.repository.SuscriptorRepository;
import com.acueducto.facturacion.web.form.SuscriptorForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuscriptorService {
    private final SuscriptorRepository repository;

    public SuscriptorService(SuscriptorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Suscriptor> listar() {
        return repository.findAllByOrderByNombreCompletoAsc();
    }

    @Transactional(readOnly = true)
    public SuscriptorForm obtenerForm(Long id) {
        Suscriptor s = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscriptor no encontrado"));
        SuscriptorForm form = new SuscriptorForm();
        form.setId(s.getId());
        form.setDocumento(s.getDocumento());
        form.setNombreCompleto(s.getNombreCompleto());
        form.setTelefono(s.getTelefono());
        form.setEmail(s.getEmail());
        form.setDireccionCorrespondencia(s.getDireccionCorrespondencia());
        form.setActivo(s.isActivo());
        return form;
    }

    @Transactional
    public Suscriptor guardar(SuscriptorForm form) {
        String documento = limpiar(form.getDocumento());
        String nombre = limpiar(form.getNombreCompleto());

        boolean duplicado = form.getId() == null
                ? repository.existsByDocumentoIgnoreCase(documento)
                : repository.existsByDocumentoIgnoreCaseAndIdNot(documento, form.getId());
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un suscriptor con ese documento");
        }

        Suscriptor entidad;
        if (form.getId() == null) {
            entidad = new Suscriptor(documento, nombre);
        } else {
            entidad = repository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Suscriptor no encontrado"));
        }

        entidad.actualizarDatos(
                documento,
                nombre,
                limpiarNullable(form.getTelefono()),
                limpiarNullable(form.getEmail()),
                limpiarNullable(form.getDireccionCorrespondencia()),
                form.isActivo()
        );
        return repository.save(entidad);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        Suscriptor s = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscriptor no encontrado"));
        s.setActivo(!s.isActivo());
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarNullable(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }
}
