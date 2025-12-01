package com.example.codersfree.controller;

import com.example.codersfree.config.IzipayConfig;
import com.example.codersfree.model.Course;
import com.example.codersfree.model.Enrollment; // Importante
import com.example.codersfree.model.User;
import com.example.codersfree.repository.EnrollmentRepository; // Importante
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
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

    @Autowired
    private EnrollmentRepository enrollmentRepository; // Inyectado

    @GetMapping
    public String index(Model model, Principal principal) {
        if (cartService.getCount() == 0) return "redirect:/cart";
        User user = userService.findByEmail(principal.getName());
        try {
            String formToken = izipayService.generateFormToken(user);
            model.addAttribute("formToken", formToken);
            model.addAttribute("publicKey", izipayConfig.getPublicKey());
            model.addAttribute("total", cartService.getTotal());
        } catch (Exception e) {
            model.addAttribute("error", "Error iniciando pasarela: " + e.getMessage());
            return "redirect:/cart";
        }
        return "checkout/index";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam Map<String, String> params, Principal principal, RedirectAttributes redirectAttributes) {
        if (!izipayService.validateHash(params)) {
            redirectAttributes.addFlashAttribute("error", "Firma inválida.");
            return "redirect:/checkout";
        }
        String krAnswer = params.get("kr-answer");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String orderStatus = mapper.readTree(krAnswer).path("orderStatus").asText();
            if (!"PAID".equals(orderStatus)) {
                redirectAttributes.addFlashAttribute("error", "Pago no exitoso: " + orderStatus);
                return "redirect:/checkout";
            }

            // --- LÓGICA DE INSCRIPCIÓN (ENROLLMENT) ---
            User user = userService.findByEmail(principal.getName());
            
            for (Course course : cartService.getItems().values()) {
                if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
                    Enrollment enrollment = Enrollment.builder()
                            .user(user)
                            .course(course)
                            .amount(course.getPrice().getValue()) // PRECIO
                            .date(LocalDateTime.now())            // FECHA
                            .build();
                    enrollmentRepository.save(enrollment);
                }
            }
            
            cartService.clear();
            redirectAttributes.addFlashAttribute("success", "¡Compra exitosa!");
            return "redirect:/checkout/success";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error procesando pago.");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/success")
    public String successPage() { return "checkout/success"; }
}