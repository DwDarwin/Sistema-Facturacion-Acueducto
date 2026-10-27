package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    List<Tarifa> findAllByOrderByNombreAsc();
    List<Tarifa> findByActivaTrueOrderByNombreAsc();
}
