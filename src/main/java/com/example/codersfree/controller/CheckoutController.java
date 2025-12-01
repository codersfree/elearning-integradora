package com.example.codersfree.controller;

import com.example.codersfree.config.IzipayConfig;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.User;
import com.example.codersfree.service.CartService;
import com.example.codersfree.service.IzipayService;
import com.example.codersfree.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private IzipayService izipayService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private IzipayConfig izipayConfig;

    @GetMapping
    public String index(Model model, Principal principal) {
        // 1. Validar carrito
        if (cartService.getCount() == 0) {
            return "redirect:/cart";
        }

        // 2. Obtener usuario
        User user = userService.findByEmail(principal.getName());

        // 3. Generar Token (Llama a Izipay)
        try {
            String formToken = izipayService.generateFormToken(user);
            
            // Pasar datos a la vista
            model.addAttribute("formToken", formToken);
            model.addAttribute("publicKey", izipayConfig.getPublicKey()); // ¡CRUCIAL PARA LA VISTA!
            model.addAttribute("total", cartService.getTotal());
            
        } catch (Exception e) {
            model.addAttribute("error", "Error iniciando pasarela de pago: " + e.getMessage());
            return "redirect:/cart";
        }

        return "checkout/index";
    }

    @PostMapping("/process")
    public String processPayment(
            @RequestParam Map<String, String> params, 
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // 1. Validar Hash
        if (!izipayService.validateHash(params)) {
            redirectAttributes.addFlashAttribute("error", "Error de seguridad: Firma inválida.");
            return "redirect:/checkout";
        }

        // 2. Verificar estado PAID
        String krAnswer = params.get("kr-answer");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String orderStatus = mapper.readTree(krAnswer).path("orderStatus").asText();

            if (!"PAID".equals(orderStatus)) {
                redirectAttributes.addFlashAttribute("error", "El pago no fue exitoso. Estado: " + orderStatus);
                return "redirect:/checkout";
            }

            // 3. ÉXITO: Inscribir usuario
            User user = userService.findByEmail(principal.getName());
            
            for (Course course : cartService.getItems().values()) {
                user.addCourse(course);
            }
            
            userService.save(user); // Guarda la relación en BD
            cartService.clear(); // Limpia carrito

            redirectAttributes.addFlashAttribute("success", "¡Compra exitosa! Bienvenido.");
            return "redirect:/checkout/success";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error procesando el pago.");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/success")
    public String successPage() {
        return "checkout/success";
    }
}