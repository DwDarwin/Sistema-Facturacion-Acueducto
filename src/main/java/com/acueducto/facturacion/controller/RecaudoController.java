package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.entity.CuentaServicio;
import com.acueducto.facturacion.enums.EstadoCuenta;
import com.acueducto.facturacion.enums.MedioPago;
import com.acueducto.facturacion.repository.CuentaServicioRepository;
import com.acueducto.facturacion.repository.FacturaRepository;
import com.acueducto.facturacion.repository.PagoRepository;
import com.acueducto.facturacion.service.RecaudoService;
import com.acueducto.facturacion.web.form.RecaudoForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/recaudos")
public class RecaudoController {
    public static class CuentaCartera {
        private final Long id;
        private final String codigo;
        private final String suscriptor;
        private final String direccion;
        private final BigDecimal saldo;

        public CuentaCartera(Long id, String codigo, String suscriptor, String direccion, BigDecimal saldo) {
            this.id = id;
            this.codigo = codigo;
            this.suscriptor = suscriptor;
            this.direccion = direccion;
            this.saldo = saldo;
        }

        public Long getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getSuscriptor() { return suscriptor; }
        public String getDireccion() { return direccion; }
        public BigDecimal getSaldo() { return saldo; }
    }

    private final RecaudoService recaudoService;
    private final CuentaServicioRepository cuentaRepository;
    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;

    public RecaudoController(RecaudoService recaudoService,
                             CuentaServicioRepository cuentaRepository,
                             FacturaRepository facturaRepository,
                             PagoRepository pagoRepository) {
        this.recaudoService = recaudoService;
        this.cuentaRepository = cuentaRepository;
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RecaudoForm());
        }
        cargar(model);
        return "recaudos/index";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("form") RecaudoForm form,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargar(model);
            return "recaudos/index";
        }
        try {
            var pago = recaudoService.registrarPago(form.getCuentaId(), form.getValor(), form.getMedioPago(), form.getReferencia());
            redirectAttributes.addFlashAttribute("mensaje", "Recaudo registrado por " + pago.getValor());
            return "redirect:/recaudos";
        } catch (IllegalArgumentException | IllegalStateException e) {
            result.reject("recaudo", e.getMessage());
            cargar(model);
            return "recaudos/index";
        }
    }

    private void cargar(Model model) {
        List<CuentaServicio> activas = cuentaRepository.findAllByOrderByCodigoCuentaAsc().stream()
                .filter(c -> c.getEstado() == EstadoCuenta.ACTIVA)
                .toList();
        List<CuentaCartera> cartera = activas.stream()
                .map(c -> new CuentaCartera(
                        c.getId(), c.getCodigoCuenta(), c.getSuscriptor().getNombreCompleto(),
                        c.getDireccionServicio(), valorSeguro(facturaRepository.calcularSaldoCuenta(c.getId()))))
                .filter(c -> c.getSaldo().signum() > 0)
                .toList();
        model.addAttribute("cuentasCartera", cartera);
        model.addAttribute("medios", MedioPago.values());
        model.addAttribute("pagos", pagoRepository.findTop100ByOrderByFechaPagoDesc());
    }

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
