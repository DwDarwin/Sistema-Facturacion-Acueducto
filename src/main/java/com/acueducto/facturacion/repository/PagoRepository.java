package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.Pago;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @EntityGraph(attributePaths = {"cuentaServicio", "cuentaServicio.suscriptor"})
    List<Pago> findTop100ByOrderByFechaPagoDesc();
}
