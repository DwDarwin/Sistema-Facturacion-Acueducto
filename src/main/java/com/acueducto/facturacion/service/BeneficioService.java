package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.AsignacionBeneficio;
import com.acueducto.facturacion.entity.Beneficio;
import com.acueducto.facturacion.entity.CuentaServicio;
import com.acueducto.facturacion.enums.ModalidadBeneficio;
import com.acueducto.facturacion.enums.TipoCalculoBeneficio;
import com.acueducto.facturacion.repository.AsignacionBeneficioRepository;
import com.acueducto.facturacion.repository.BeneficioRepository;
import com.acueducto.facturacion.repository.CuentaServicioRepository;
import com.acueducto.facturacion.web.form.AsignacionBeneficioForm;
import com.acueducto.facturacion.web.form.BeneficioForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BeneficioService {
    private final BeneficioRepository beneficioRepository;
    private final AsignacionBeneficioRepository asignacionRepository;
    private final CuentaServicioRepository cuentaRepository;

    public BeneficioService(BeneficioRepository beneficioRepository,
                            AsignacionBeneficioRepository asignacionRepository,
                            CuentaServicioRepository cuentaRepository) {
        this.beneficioRepository = beneficioRepository;
        this.asignacionRepository = asignacionRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Transactional(readOnly = true)
    public List<Beneficio> listar() {
        return beneficioRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<AsignacionBeneficio> listarAsignaciones() {
        return asignacionRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public BeneficioForm obtenerForm(Long id) {
        Beneficio b = beneficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado"));
        BeneficioForm form = new BeneficioForm();
        form.setId(b.getId());
        form.setNombre(b.getNombre());
        form.setTipo(b.getTipo());
        form.setModalidad(b.getModalidad());
        form.setTipoCalculo(b.getTipoCalculo());
        form.setValor(b.getValor());
        form.setVigenteDesde(b.getVigenteDesde());
        form.setVigenteHasta(b.getVigenteHasta());
        form.setActivo(b.isActivo());
        return form;
    }

    @Transactional
    public Beneficio guardar(BeneficioForm form) {
        if (form.getModalidad() == ModalidadBeneficio.TEMPORAL && form.getVigenteHasta() == null) {
            throw new IllegalArgumentException("Un beneficio temporal debe tener fecha final");
        }
        if (form.getVigenteHasta() != null && form.getVigenteHasta().isBefore(form.getVigenteDesde())) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial");
        }
        if (form.getTipoCalculo() == TipoCalculoBeneficio.PORCENTAJE
                && form.getValor().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Un porcentaje no puede superar el 100 %");
        }

        if (form.getModalidad() == ModalidadBeneficio.RECURRENTE) {
            form.setVigenteHasta(null);
        }

        Beneficio entidad;
        if (form.getId() == null) {
            entidad = new Beneficio(
                    form.getNombre().trim(), form.getTipo(), form.getModalidad(), form.getTipoCalculo(),
                    form.getValor().setScale(2, RoundingMode.HALF_UP), form.getVigenteDesde(), form.getVigenteHasta()
            );
        } else {
            entidad = beneficioRepository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado"));
        }

        entidad.actualizar(
                form.getNombre().trim(), form.getTipo(), form.getModalidad(), form.getTipoCalculo(),
                form.getValor().setScale(2, RoundingMode.HALF_UP), form.getVigenteDesde(),
                form.getVigenteHasta(), form.isActivo()
        );
        return beneficioRepository.save(entidad);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        Beneficio b = beneficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado"));
        b.setActivo(!b.isActivo());
    }

    @Transactional
    public void asignar(AsignacionBeneficioForm form) {
        if (asignacionRepository.existsByCuentaServicioIdAndBeneficioId(form.getCuentaId(), form.getBeneficioId())) {
            throw new IllegalArgumentException("Ese beneficio ya fue asignado a la cuenta");
        }
        CuentaServicio cuenta = cuentaRepository.findById(form.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de servicio no encontrada"));
        Beneficio beneficio = beneficioRepository.findById(form.getBeneficioId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado"));
        asignacionRepository.save(new AsignacionBeneficio(cuenta, beneficio));
    }

    @Transactional
    public void cambiarEstadoAsignacion(Long id) {
        AsignacionBeneficio a = asignacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));
        a.setActiva(!a.isActiva());
    }
}
