package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.Suscriptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuscriptorRepository extends JpaRepository<Suscriptor, Long> {
    List<Suscriptor> findAllByOrderByNombreCompletoAsc();
    List<Suscriptor> findByActivoTrueOrderByNombreCompletoAsc();
    boolean existsByDocumentoIgnoreCase(String documento);
    boolean existsByDocumentoIgnoreCaseAndIdNot(String documento, Long id);
}
