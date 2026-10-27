package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.entity.Factura;
import com.acueducto.facturacion.enums.TipoBeneficio;
import com.acueducto.facturacion.repository.FacturaBeneficioRepository;
import com.acueducto.facturacion.repository.FacturaRepository;
import com.acueducto.facturacion.service.FacturacionService;
import com.acueducto.facturacion.web.view.FacturaImpresionView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/facturas")
public class FacturaController {
    private final FacturaRepository facturaRepository;
    private final FacturaBeneficioRepository facturaBeneficioRepository;
    private final FacturacionService facturacionService;

    public FacturaController(FacturaRepository facturaRepository,
                             FacturaBeneficioRepository facturaBeneficioRepository,
                             FacturacionService facturacionService) {
        this.facturaRepository = facturaRepository;
        this.facturaBeneficioRepository = facturaBeneficioRepository;
        this.facturacionService = facturacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("facturas", facturaRepository.findAllByOrderByFechaEmisionDesc());
        return "facturas/lista";
    }

    @PostMapping("/generar")
    public String generar(@RequestParam int anio, @RequestParam int mes) {
        facturacionService.generarPeriodo(YearMonth.of(anio, mes));
        return "redirect:/facturas";
    }

    @GetMapping("/imprimir-periodo")
    public String imprimirPeriodo(@RequestParam int anio, @RequestParam int mes, Model model) {
        List<FacturaImpresionView> items = facturaRepository
                .findByPeriodoAnioAndPeriodoMesOrderByCuentaServicioCodigoCuentaAsc(anio, mes)
                .stream()
                .map(factura -> {
                    BigDecimal carteraAnterior = facturacionService.carteraAnterior(factura);
                    return new FacturaImpresionView(
                            factura,
                            facturaBeneficioRepository.findByFacturaId(factura.getId()),
                            carteraAnterior,
                            factura.getSaldo().add(carteraAnterior)
                    );
                })
                .toList();

        model.addAttribute("items", items);
        model.addAttribute("anio", anio);
        model.addAttribute("mes", mes);
        return "facturas/imprimir-periodo";
    }

    @GetMapping("/{id}/imprimir")
    public String imprimir(@PathVariable Long id, Model model) {
        Factura factura = facturaRepository.findDetalleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        BigDecimal carteraAnterior = facturacionService.carteraAnterior(factura);
        BigDecimal totalAPagar = factura.getSaldo().add(carteraAnterior);

        model.addAttribute("factura", factura);
        model.addAttribute("beneficios", facturaBeneficioRepository.findByFacturaId(id));
        model.addAttribute("carteraAnterior", carteraAnterior);
        model.addAttribute("totalAPagar", totalAPagar);
        return "facturas/imprimir";
    }

    @PostMapping("/{id}/beneficio-ocasional")
    public String beneficioOcasional(@PathVariable Long id,
                                     @RequestParam TipoBeneficio tipo,
                                     @RequestParam String concepto,
                                     @RequestParam BigDecimal valor) {
        facturacionService.aplicarBeneficioOcasional(id, tipo, concepto, valor);
        return "redirect:/facturas/" + id + "/imprimir";
    }
}
