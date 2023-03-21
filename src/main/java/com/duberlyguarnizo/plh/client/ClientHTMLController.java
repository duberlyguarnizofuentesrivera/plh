package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/system/clients")
@Controller
public class ClientHTMLController {

    @GetMapping("/list")
    public String listAllClients(Model model) {
        model.addAttribute("typesList", PersonType.values());
        model.addAttribute("statusList", UserStatus.values());
        return "system/clients/list";
    }
}
