package com.example.codersfree.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.codersfree.dto.PriceDto;
import com.example.codersfree.model.Price;
import com.example.codersfree.service.PriceService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/prices")
public class PriceAdminController {

    @Autowired
    private PriceService priceService;
    
    @GetMapping
    public String index(
        Model model,
        @PageableDefault(size = 10, page = 0, sort = "value", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        model.addAttribute("prices", priceService.paginate(pageable));
        return "admin/prices/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("priceDto")) {
            model.addAttribute("priceDto", new PriceDto());
        }
        return "admin/prices/create";
    }

    @PostMapping("/create")
    public String store(
        @Valid @ModelAttribute("priceDto") PriceDto priceDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.priceDto", bindingResult);
            redirectAttributes.addFlashAttribute("priceDto", priceDto);
            return "redirect:/admin/prices/create"; 
        }

        try {
            priceService.save(priceDto);
            redirectAttributes.addFlashAttribute("success", "¡Precio creado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el precio: " + e.getMessage());
        }

        return "redirect:/admin/prices";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Price price = priceService.findById(id);

        if (!model.containsAttribute("priceDto")) {
            PriceDto priceDto = new PriceDto(price.getName(), price.getValue());
            model.addAttribute("priceDto", priceDto);
        }
        model.addAttribute("price", price);
        return "admin/prices/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable("id") Long id, 
        @Valid @ModelAttribute("priceDto") PriceDto priceDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.priceDto", bindingResult);
            redirectAttributes.addFlashAttribute("priceDto", priceDto);
            return "redirect:/admin/prices/" + id + "/edit"; 
        }

        try {
            priceService.update(id, priceDto);
            redirectAttributes.addFlashAttribute("success", "¡Precio actualizado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el precio: " + e.getMessage());
        }

        return "redirect:/admin/prices/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            priceService.delete(id);
            redirectAttributes.addFlashAttribute("success", "¡Precio eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el precio: " + e.getMessage());
        }
        return "redirect:/admin/prices";
    }
}