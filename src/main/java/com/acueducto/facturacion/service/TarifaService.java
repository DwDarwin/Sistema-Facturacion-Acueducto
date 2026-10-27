package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.Tarifa;
import com.acueducto.facturacion.repository.TarifaRepository;
import com.acueducto.facturacion.web.form.TarifaForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;

@Service
public class TarifaService {
    private final TarifaRepository repository;

    public TarifaService(TarifaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Tarifa> listar() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public TarifaForm obtenerForm(Long id) {
        Tarifa t = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
        TarifaForm form = new TarifaForm();
        form.setId(t.getId());
        form.setNombre(t.getNombre());
        form.setValorMensual(t.getValorMensual());
        form.setVigenteDesde(t.getVigenteDesde());
        form.setVigenteHasta(t.getVigenteHasta());
        form.setActiva(t.isActiva());
        return form;
    }

    @Transactional
    public Tarifa guardar(TarifaForm form) {
        if (form.getVigenteHasta() != null && form.getVigenteHasta().isBefore(form.getVigenteDesde())) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial");
        }

        Tarifa entidad;
        if (form.getId() == null) {
            entidad = new Tarifa(
                    form.getNombre().trim(),
                    form.getValorMensual().setScale(2, RoundingMode.HALF_UP),
                    form.getVigenteDesde()
            );
        } else {
            entidad = repository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
        }

        entidad.actualizar(
                form.getNombre().trim(),
                form.getValorMensual().setScale(2, RoundingMode.HALF_UP),
                form.getVigenteDesde(),
                form.getVigenteHasta(),
                form.isActiva()
        );
        return repository.save(entidad);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        Tarifa t = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
        t.setActiva(!t.isActiva());
    }
}
