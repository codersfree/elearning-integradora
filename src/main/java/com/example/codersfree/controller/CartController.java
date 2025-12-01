package com.example.codersfree.controller;

import com.example.codersfree.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("items", cartService.getItems().values());
        model.addAttribute("total", cartService.getTotal());
        return "cart/index";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam Long courseId, 
            @RequestParam String slug, 
            @RequestParam String action, // Recibimos si es "add" o "buy"
            RedirectAttributes redirectAttributes) {

        cartService.addItem(courseId);

        if ("buy".equals(action)) {
            // Caso: Comprar ahora -> Ir al carrito
            return "redirect:/cart";
        } else {
            // Caso: Añadir al carrito -> Volver al curso con mensaje
            redirectAttributes.addFlashAttribute("success", "Curso agregado al carrito correctamente.");
            return "redirect:/courses/" + slug;
        }
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Long id) {
        cartService.removeItem(id);
        return "redirect:/cart";
    }

    @PostMapping("/remove-from-course")
    public String removeFromCourse(
            @RequestParam Long courseId, 
            @RequestParam String slug,
            RedirectAttributes redirectAttributes) {
        
        cartService.removeItem(courseId);
        
        redirectAttributes.addFlashAttribute("success", "Curso eliminado del carrito.");
        return "redirect:/courses/" + slug;
    }
}