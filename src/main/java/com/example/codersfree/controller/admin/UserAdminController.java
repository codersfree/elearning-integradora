package com.example.codersfree.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class UserAdminController {

    @GetMapping("/users")
    public String index(Model model) {

        return "admin/users/index";
    }

}
