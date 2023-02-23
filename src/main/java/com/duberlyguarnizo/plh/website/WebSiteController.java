package com.duberlyguarnizo.plh.website;

import com.duberlyguarnizo.plh.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSiteController {
    private final UserService service;

    @Autowired
    public WebSiteController(UserService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String doLogIn() {
        return "login";
    }


}
