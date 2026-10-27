package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.enums.EstadoCuenta;
import com.acueducto.facturacion.enums.EstadoFactura;
import com.acueducto.facturacion.repository.CuentaServicioRepository;
import com.acueducto.facturacion.repository.FacturaRepository;
import com.acueducto.facturacion.repository.SuscriptorRepository;
import com.acueducto.facturacion.repository.TarifaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Controller
public class HomeController {
    private final SuscriptorRepository suscriptorRepository;
    private final CuentaServicioRepository cuentaRepository;
    private final TarifaRepository tarifaRepository;
    private final FacturaRepository facturaRepository;

    public HomeController(SuscriptorRepository suscriptorRepository,
                          CuentaServicioRepository cuentaRepository,
                          TarifaRepository tarifaRepository,
                          FacturaRepository facturaRepository) {
        this.suscriptorRepository = suscriptorRepository;
        this.cuentaRepository = cuentaRepository;
        this.tarifaRepository = tarifaRepository;
        this.facturaRepository = facturaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
        model.addAttribute("suscriptores", suscriptorRepository.count());
        model.addAttribute("cuentasActivas", cuentaRepository.countByEstado(EstadoCuenta.ACTIVA));
        model.addAttribute("tarifas", tarifaRepository.count());
        model.addAttribute("facturas", facturaRepository.count());
        BigDecimal cartera = facturaRepository.totalCarteraPendiente();
        model.addAttribute("cartera", cartera == null ? BigDecimal.ZERO : cartera);
        model.addAttribute("morosos", facturaRepository.countBySaldoGreaterThanAndFechaVencimientoBeforeAndEstadoNot(
                BigDecimal.ZERO, hoy, EstadoFactura.ANULADA));
        return "index";
    }
}
