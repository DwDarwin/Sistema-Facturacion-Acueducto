package com.acueducto.facturacion.controller;

import com.acueducto.facturacion.service.SuscriptorService;
import com.acueducto.facturacion.web.form.SuscriptorForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suscriptores")
public class SuscriptorController {
    private final SuscriptorService service;

    public SuscriptorController(SuscriptorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("suscriptores", service.listar());
        return "suscriptores/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SuscriptorForm());
        }
        model.addAttribute("titulo", "Nuevo suscriptor");
        return "suscriptores/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") SuscriptorForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nuevo suscriptor");
            return "suscriptores/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Suscriptor creado correctamente");
            return "redirect:/suscriptores";
        } catch (IllegalArgumentException e) {
            result.reject("suscriptor", e.getMessage());
            model.addAttribute("titulo", "Nuevo suscriptor");
            return "suscriptores/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.obtenerForm(id));
        model.addAttribute("titulo", "Editar suscriptor");
        return "suscriptores/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") SuscriptorForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar suscriptor");
            return "suscriptores/formulario";
        }
        try {
            service.guardar(form);
            redirectAttributes.addFlashAttribute("mensaje", "Suscriptor actualizado correctamente");
            return "redirect:/suscriptores";
        } catch (IllegalArgumentException e) {
            result.reject("suscriptor", e.getMessage());
            model.addAttribute("titulo", "Editar suscriptor");
            return "suscriptores/formulario";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado del suscriptor actualizado");
        return "redirect:/suscriptores";
    }
}
