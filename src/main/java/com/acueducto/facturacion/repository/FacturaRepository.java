package com.acueducto.facturacion.repository;

import com.acueducto.facturacion.entity.Factura;
import com.acueducto.facturacion.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    @EntityGraph(attributePaths = {"cuentaServicio", "cuentaServicio.suscriptor"})
    Optional<Factura> findDetalleById(Long id);

    @EntityGraph(attributePaths = {"cuentaServicio", "cuentaServicio.suscriptor"})
    List<Factura> findAllByOrderByFechaEmisionDesc();

    @EntityGraph(attributePaths = {"cuentaServicio", "cuentaServicio.suscriptor"})
    List<Factura> findByPeriodoAnioAndPeriodoMesOrderByCuentaServicioCodigoCuentaAsc(int anio, int mes);

    boolean existsByCuentaServicioIdAndPeriodoAnioAndPeriodoMes(Long cuentaId, int anio, int mes);

    Optional<Factura> findByCuentaServicioIdAndPeriodoAnioAndPeriodoMes(Long cuentaId, int anio, int mes);

    List<Factura> findByCuentaServicioIdAndSaldoGreaterThanOrderByFechaEmisionAsc(Long cuentaId, BigDecimal saldo);

    List<Factura> findBySaldoGreaterThanAndFechaVencimientoBeforeAndEstadoNot(
            BigDecimal saldo, LocalDate fecha, EstadoFactura estado);

    @Query("""
        select coalesce(sum(f.saldo), 0)
        from Factura f
        where f.cuentaServicio.id = :cuentaId
          and f.id <> :facturaId
          and f.fechaEmision < :fechaEmision
          and f.saldo > 0
          and f.estado <> com.acueducto.facturacion.enums.EstadoFactura.ANULADA
        """)
    BigDecimal calcularCarteraAnterior(@Param("cuentaId") Long cuentaId,
                                       @Param("facturaId") Long facturaId,
                                       @Param("fechaEmision") LocalDate fechaEmision);

    @Query("""
        select coalesce(sum(f.saldo), 0)
        from Factura f
        where f.saldo > 0
          and f.estado <> com.acueducto.facturacion.enums.EstadoFactura.ANULADA
        """)
    BigDecimal totalCarteraPendiente();

    @Query("""
        select coalesce(sum(f.saldo), 0)
        from Factura f
        where f.cuentaServicio.id = :cuentaId
          and f.saldo > 0
          and f.estado <> com.acueducto.facturacion.enums.EstadoFactura.ANULADA
        """)
    BigDecimal calcularSaldoCuenta(@Param("cuentaId") Long cuentaId);

    long countBySaldoGreaterThanAndFechaVencimientoBeforeAndEstadoNot(
            BigDecimal saldo, LocalDate fecha, EstadoFactura estado);
}
