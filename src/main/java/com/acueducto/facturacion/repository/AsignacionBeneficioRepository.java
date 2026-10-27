package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.AsignacionBeneficio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionBeneficioRepository extends JpaRepository<AsignacionBeneficio, Long> {
    List<AsignacionBeneficio> findByCuentaServicioIdAndActivaTrue(Long cuentaId);

    @EntityGraph(attributePaths = {"cuentaServicio", "cuentaServicio.suscriptor", "beneficio"})
    List<AsignacionBeneficio> findAllByOrderByIdDesc();

    boolean existsByCuentaServicioIdAndBeneficioId(Long cuentaId, Long beneficioId);
}
