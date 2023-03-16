package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/system/users/")
@Slf4j
public class UserHTMLController {
    private final UserService userService;

    @Autowired
    public UserHTMLController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/list")
    public String getAllUser(Model model) {
        model.addAttribute("statusList", UserStatus.values());
        model.addAttribute("roleList", UserRole.values());
        //Filters are processed using API controller, with JS.
        return "/system/users/list";
    }


    @GetMapping("/crud/by-username/{username}")
    public String getUsersByUserName() {

        return "/system/users/crud/detail";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String getProfile() {
        return "/system/profile";
    }
}
