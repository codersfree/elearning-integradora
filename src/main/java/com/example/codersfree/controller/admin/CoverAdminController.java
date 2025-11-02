package com.example.codersfree.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class CoverAdminController {

    @GetMapping("/covers")
    public String index(Model model) {

        return "admin/covers/index";
    }

}
