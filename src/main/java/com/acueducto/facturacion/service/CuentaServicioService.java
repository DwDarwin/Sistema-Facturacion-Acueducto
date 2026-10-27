package com.acueducto.facturacion.service;

import com.acueducto.facturacion.entity.CuentaServicio;
import com.acueducto.facturacion.entity.Suscriptor;
import com.acueducto.facturacion.entity.Tarifa;
import com.acueducto.facturacion.enums.EstadoCuenta;
import com.acueducto.facturacion.repository.CuentaServicioRepository;
import com.acueducto.facturacion.repository.SuscriptorRepository;
import com.acueducto.facturacion.repository.TarifaRepository;
import com.acueducto.facturacion.web.form.CuentaServicioForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CuentaServicioService {
    private final CuentaServicioRepository cuentaRepository;
    private final SuscriptorRepository suscriptorRepository;
    private final TarifaRepository tarifaRepository;

    public CuentaServicioService(CuentaServicioRepository cuentaRepository,
                                 SuscriptorRepository suscriptorRepository,
                                 TarifaRepository tarifaRepository) {
        this.cuentaRepository = cuentaRepository;
        this.suscriptorRepository = suscriptorRepository;
        this.tarifaRepository = tarifaRepository;
    }

    @Transactional(readOnly = true)
    public List<CuentaServicio> listar() {
        return cuentaRepository.findAllByOrderByCodigoCuentaAsc();
    }

    @Transactional(readOnly = true)
    public CuentaServicioForm obtenerForm(Long id) {
        CuentaServicio c = cuentaRepository.findDetalleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de servicio no encontrada"));
        CuentaServicioForm form = new CuentaServicioForm();
        form.setId(c.getId());
        form.setCodigoCuenta(c.getCodigoCuenta());
        form.setSuscriptorId(c.getSuscriptor().getId());
        form.setTarifaId(c.getTarifa().getId());
        form.setDireccionServicio(c.getDireccionServicio());
        form.setFechaInicio(c.getFechaInicio());
        form.setEstado(c.getEstado());
        return form;
    }

    @Transactional
    public CuentaServicio guardar(CuentaServicioForm form) {
        Suscriptor suscriptor = suscriptorRepository.findById(form.getSuscriptorId())
                .orElseThrow(() -> new IllegalArgumentException("Suscriptor no encontrado"));
        Tarifa tarifa = tarifaRepository.findById(form.getTarifaId())
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

        CuentaServicio entidad;
        if (form.getId() == null) {
            // Código temporal único para poder obtener el ID de la base de datos.
            // En la misma transacción se sustituye inmediatamente por el código definitivo.
            String codigoTemporal = "TMP-" + UUID.randomUUID();
            entidad = new CuentaServicio(
                    codigoTemporal,
                    suscriptor,
                    tarifa,
                    form.getDireccionServicio().trim(),
                    form.getFechaInicio()
            );
            entidad.cambiarEstado(form.getEstado());
            entidad = cuentaRepository.saveAndFlush(entidad);

            String codigoDefinitivo = generarCodigoCuenta(entidad.getId());
            if (cuentaRepository.existsByCodigoCuentaIgnoreCase(codigoDefinitivo)) {
                throw new IllegalStateException("No fue posible generar un código de cuenta único");
            }
            entidad.asignarCodigoCuenta(codigoDefinitivo);
            return cuentaRepository.save(entidad);
        }

        entidad = cuentaRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de servicio no encontrada"));
        entidad.actualizar(
                suscriptor,
                tarifa,
                form.getDireccionServicio().trim(),
                form.getFechaInicio(),
                form.getEstado()
        );
        return cuentaRepository.save(entidad);
    }

    private String generarCodigoCuenta(Long id) {
        return String.format("AGP-%06d", id);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        CuentaServicio c = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de servicio no encontrada"));
        c.cambiarEstado(c.getEstado() == EstadoCuenta.ACTIVA ? EstadoCuenta.SUSPENDIDA : EstadoCuenta.ACTIVA);
    }
}
