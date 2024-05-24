package com.shopme.admin.user.controller;

import com.shopme.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class UserRestController {
    @Autowired
    private UserService service;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
