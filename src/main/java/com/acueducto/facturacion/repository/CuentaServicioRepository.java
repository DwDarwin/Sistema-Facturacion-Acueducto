package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.CuentaServicio;
import com.acueducto.facturacion.enums.EstadoCuenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaServicioRepository extends JpaRepository<CuentaServicio, Long> {
    @EntityGraph(attributePaths = {"suscriptor", "tarifa"})
    List<CuentaServicio> findAllByOrderByCodigoCuentaAsc();

    @EntityGraph(attributePaths = {"suscriptor", "tarifa"})
    Optional<CuentaServicio> findDetalleById(Long id);

    List<CuentaServicio> findByEstado(EstadoCuenta estado);
    boolean existsByCodigoCuentaIgnoreCase(String codigoCuenta);
    boolean existsByCodigoCuentaIgnoreCaseAndIdNot(String codigoCuenta, Long id);
    long countByEstado(EstadoCuenta estado);
}
