package com.duberlyguarnizo.plh.website;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.user.UserDetailDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
public class WebSystemAdminController {

    @GetMapping("/admin-panel")
    public String adminPanel() {
        return "/system/admin-panel";
    }

    @GetMapping("/user/crud/create")
    public String adminCreateUser(Model model, UserDetailDto userDetailDto) {
        model.addAttribute("roleList", UserRole.values());
        model.addAttribute("statusList", UserStatus.values());
        model.addAttribute("userDto", userDetailDto);

        return "/system/users/crud/create";
    }


}
