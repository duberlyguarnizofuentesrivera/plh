package com.duberlyguarnizo.plh.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
public class WebSystemAdminController {

    @GetMapping("/admin-panel")
    public String adminPanel() {
        return "/system/admin-panel";
    }
}
