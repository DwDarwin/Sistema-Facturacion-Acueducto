package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.FacturaBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaBeneficioRepository extends JpaRepository<FacturaBeneficio, Long> {
    List<FacturaBeneficio> findByFacturaId(Long facturaId);
}
