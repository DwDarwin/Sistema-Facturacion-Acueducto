package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    List<Beneficio> findAllByOrderByNombreAsc();
    List<Beneficio> findByActivoTrueOrderByNombreAsc();
}
