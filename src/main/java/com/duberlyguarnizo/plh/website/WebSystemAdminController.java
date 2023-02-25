package com.duberlyguarnizo.plh.website;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.user.UserRegisterDto;
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
    public String adminCreateUser(Model model, UserRegisterDto userRegisterDto) {
//        UserRegisterDto userRegisterDto = new UserRegisterDto(
//                "Nombre",
//                "Apellido",
//                "00000000",
//                null,
//                null,
//                null,
//                null,
//                null,
//                "nombreusuario",
//                "contraseña"
//        );
        model.addAttribute("roleList", UserRole.values());
        model.addAttribute("statusList", UserStatus.values());
        model.addAttribute("userDto", userRegisterDto);

        return "/system/user/crud/create";
    }


}
