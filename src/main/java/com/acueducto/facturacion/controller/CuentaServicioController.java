package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.enums.EstadoCuenta;
import com.acueducto.facturacion.repository.SuscriptorRepository;
import com.acueducto.facturacion.repository.TarifaRepository;
import com.acueducto.facturacion.service.CuentaServicioService;
import com.acueducto.facturacion.web.form.CuentaServicioForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequestMapping("/cuentas")
public class CuentaServicioController {
    private final CuentaServicioService service;
    private final SuscriptorRepository suscriptorRepository;
    private final TarifaRepository tarifaRepository;

    public CuentaServicioController(CuentaServicioService service,
                                    SuscriptorRepository suscriptorRepository,
                                    TarifaRepository tarifaRepository) {
        this.service = service;
        this.suscriptorRepository = suscriptorRepository;
        this.tarifaRepository = tarifaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cuentas", service.listar());
        return "cuentas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        CuentaServicioForm form = new CuentaServicioForm();
        form.setFechaInicio(LocalDate.now(ZoneId.of("America/Bogota")));
        form.setEstado(EstadoCuenta.ACTIVA);
        model.addAttribute("form", form);
        cargarCombos(model, true);
        model.addAttribute("titulo", "Nueva cuenta de servicio");
        return "cuentas/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") CuentaServicioForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargarCombos(model, true);
            model.addAttribute("titulo", "Nueva cuenta de servicio");
            return "cuentas/formulario";
        }
        try {
            var cuenta = service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Cuenta creada correctamente. Código generado: " + cuenta.getCodigoCuenta());
            return "redirect:/cuentas";
        } catch (IllegalArgumentException e) {
            result.reject("cuenta", e.getMessage());
            cargarCombos(model, true);
            model.addAttribute("titulo", "Nueva cuenta de servicio");
            return "cuentas/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.obtenerForm(id));
        cargarCombos(model, false);
        model.addAttribute("titulo", "Editar cuenta de servicio");
        return "cuentas/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") CuentaServicioForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (result.hasErrors()) {
            cargarCombos(model, false);
            model.addAttribute("titulo", "Editar cuenta de servicio");
            return "cuentas/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta de servicio actualizada correctamente");
            return "redirect:/cuentas";
        } catch (IllegalArgumentException e) {
            result.reject("cuenta", e.getMessage());
            cargarCombos(model, false);
            model.addAttribute("titulo", "Editar cuenta de servicio");
            return "cuentas/formulario";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de la cuenta actualizado");
        return "redirect:/cuentas";
    }

    private void cargarCombos(Model model, boolean soloActivos) {
        model.addAttribute("suscriptores", soloActivos
                ? suscriptorRepository.findByActivoTrueOrderByNombreCompletoAsc()
                : suscriptorRepository.findAllByOrderByNombreCompletoAsc());
        model.addAttribute("tarifas", soloActivos
                ? tarifaRepository.findByActivaTrueOrderByNombreAsc()
                : tarifaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("estados", EstadoCuenta.values());
    }
}
