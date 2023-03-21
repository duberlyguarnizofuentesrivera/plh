package com.duberlyguarnizo.plh.website;

import com.duberlyguarnizo.plh.user.UserBasicDto;
import com.duberlyguarnizo.plh.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class WebSiteController {
    private final UserService service;

    @Autowired
    public WebSiteController(UserService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String doLogIn(Model model,
                          @RequestParam(required = false) String error,
                          @RequestParam(required = false) String logout) {
        model.addAttribute("logout", logout != null);
        model.addAttribute("badCredentials", error != null);
        return "login";
    }

    @GetMapping("/")
    public String mainSite(Model model) {
        Optional<UserBasicDto> currentUser = service.getCurrentUser();
        model.addAttribute("user", currentUser.orElse(null));
        return "index";
    }

    @GetMapping("/system")
    public String mainSystemSite(Model model) {
        //UserBasicDto used instead of Principal argument, bc we need more fields than just the name
        Optional<UserBasicDto> currentUser = service.getCurrentUser();
        UserBasicDto user = null;
        if (currentUser.isPresent()) {
            user = currentUser.get();
        }
        model.addAttribute("user", user);
        return "system/index";
    }


}
