package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.enums.ModalidadBeneficio;
import com.acueducto.facturacion.enums.TipoBeneficio;
import com.acueducto.facturacion.enums.TipoCalculoBeneficio;
import com.acueducto.facturacion.repository.BeneficioRepository;
import com.acueducto.facturacion.repository.CuentaServicioRepository;
import com.acueducto.facturacion.service.BeneficioService;
import com.acueducto.facturacion.web.form.AsignacionBeneficioForm;
import com.acueducto.facturacion.web.form.BeneficioForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequestMapping("/beneficios")
public class BeneficioController {
    private final BeneficioService service;
    private final BeneficioRepository beneficioRepository;
    private final CuentaServicioRepository cuentaRepository;

    public BeneficioController(BeneficioService service,
                               BeneficioRepository beneficioRepository,
                               CuentaServicioRepository cuentaRepository) {
        this.service = service;
        this.beneficioRepository = beneficioRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("beneficios", service.listar());
        model.addAttribute("asignaciones", service.listarAsignaciones());
        model.addAttribute("asignacionForm", new AsignacionBeneficioForm());
        model.addAttribute("cuentas", cuentaRepository.findAllByOrderByCodigoCuentaAsc());
        model.addAttribute("beneficiosActivos", beneficioRepository.findByActivoTrueOrderByNombreAsc());
        return "beneficios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        BeneficioForm form = new BeneficioForm();
        form.setActivo(true);
        form.setVigenteDesde(LocalDate.now(ZoneId.of("America/Bogota")));
        model.addAttribute("form", form);
        cargarCatalogos(model);
        model.addAttribute("titulo", "Nuevo descuento o subsidio");
        return "beneficios/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") BeneficioForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("titulo", "Nuevo descuento o subsidio");
            return "beneficios/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Beneficio creado correctamente");
            return "redirect:/beneficios";
        } catch (IllegalArgumentException e) {
            result.reject("beneficio", e.getMessage());
            cargarCatalogos(model);
            model.addAttribute("titulo", "Nuevo descuento o subsidio");
            return "beneficios/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.obtenerForm(id));
        cargarCatalogos(model);
        model.addAttribute("titulo", "Editar descuento o subsidio");
        return "beneficios/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") BeneficioForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (result.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("titulo", "Editar descuento o subsidio");
            return "beneficios/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Beneficio actualizado correctamente");
            return "redirect:/beneficios";
        } catch (IllegalArgumentException e) {
            result.reject("beneficio", e.getMessage());
            cargarCatalogos(model);
            model.addAttribute("titulo", "Editar descuento o subsidio");
            return "beneficios/formulario";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado del beneficio actualizado");
        return "redirect:/beneficios";
    }

    @PostMapping("/asignar")
    public String asignar(@Valid @ModelAttribute("asignacionForm") AsignacionBeneficioForm form,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar una cuenta y un beneficio");
            return "redirect:/beneficios";
        }
        try {
            service.asignar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Beneficio asignado a la cuenta");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/beneficios";
    }

    @PostMapping("/asignaciones/{id}/estado")
    public String cambiarEstadoAsignacion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cambiarEstadoAsignacion(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de la asignación actualizado");
        return "redirect:/beneficios";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tipos", TipoBeneficio.values());
        model.addAttribute("modalidades", ModalidadBeneficio.values());
        model.addAttribute("tiposCalculo", TipoCalculoBeneficio.values());
    }
}
