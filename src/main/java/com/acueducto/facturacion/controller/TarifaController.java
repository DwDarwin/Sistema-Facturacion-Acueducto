package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.service.TarifaService;
import com.acueducto.facturacion.web.form.TarifaForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tarifas")
public class TarifaController {
    private final TarifaService service;

    public TarifaController(TarifaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tarifas", service.listar());
        return "tarifas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        if (!model.containsAttribute("form")) {
            TarifaForm form = new TarifaForm();
            form.setActiva(true);
            model.addAttribute("form", form);
        }
        model.addAttribute("titulo", "Nueva tarifa");
        return "tarifas/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") TarifaForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nueva tarifa");
            return "tarifas/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Tarifa creada correctamente");
            return "redirect:/tarifas";
        } catch (IllegalArgumentException e) {
            result.reject("tarifa", e.getMessage());
            model.addAttribute("titulo", "Nueva tarifa");
            return "tarifas/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.obtenerForm(id));
        model.addAttribute("titulo", "Editar tarifa");
        return "tarifas/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") TarifaForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar tarifa");
            return "tarifas/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Tarifa actualizada correctamente");
            return "redirect:/tarifas";
        } catch (IllegalArgumentException e) {
            result.reject("tarifa", e.getMessage());
            model.addAttribute("titulo", "Editar tarifa");
            return "tarifas/formulario";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de la tarifa actualizado");
        return "redirect:/tarifas";
    }
}
