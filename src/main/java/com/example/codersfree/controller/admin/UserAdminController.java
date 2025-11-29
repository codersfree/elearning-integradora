package com.example.codersfree.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class UserAdminController {

    @GetMapping("/users")
    public String index(
        Model model,
        @PageableDefault(
            size = 10, 
            page = 0,
            sort = "id",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {


        

        return "admin/users/index";
    }

}
