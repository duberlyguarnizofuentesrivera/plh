package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/system/user/")
@Slf4j
public class UserHTMLController {
    private final UserService userService;

    @Autowired
    public UserHTMLController(UserService userService) {
        this.userService = userService;
    }

    //get current user
    @GetMapping("/current-user")
    public UserBasicDto loggedUser() {
        return userService.getCurrentUser().orElse(null);

    }

    //CRUD methods
    @PostMapping(path = "/crud/create-action")
    public String createUser(@ModelAttribute UserRegisterDto userRegister, Model model) {
        boolean result = userService.save(userRegister);
        if (result) {
            return "redirect:/system/user/crud/by-username/" + userRegister.username();
        } else {
            return "redirect:/system/crud-error";
        }
    }

    @PostMapping("/update")
    public boolean updateUser(@RequestBody UserRegisterDto userRegister) {
        return userService.update(userRegister, true);
    }

    @GetMapping("/delete/{username}")
    public boolean deleteUser(@PathVariable("username") String username) {
        return userService.deleteByUserName(username);
    }

    @GetMapping("/list")
    public String getAllUser(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size, Model model) {
        Page<UserBasicDto> result = userService.findAll(page, size);
        result.getTotalPages();
        model.addAttribute("queryResult", result);
        return "/system/user/list";
    }

    @GetMapping("/by-role/{role}")
    public Page<UserBasicDto> getAllUsersByRole(@PathVariable("role") String role, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            UserRole userRole = UserRole.valueOf(role);
            return userService.findByRole(userRole, page, size);
        } catch (IllegalArgumentException e) {
            return Page.empty();
        }
    }

    @GetMapping("/by-status/{status}")
    public Page<UserBasicDto> getAllUsersByUserStatus(@PathVariable("status") String status, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status);
            return userService.findByStatus(userStatus, page, size);
        } catch (IllegalArgumentException e) {
            return Page.empty();
        }
    }

    @GetMapping("/crud/by-username/{username}")
    public String getUsersByUserName(@PathVariable("username") String username, Model model) {
        UserDto userDto = userService.getByUsername(username).orElse(null);
        model.addAttribute("userDto", userDto);
        if (userDto != null && userDto.createdBy() != null && userDto.lastModifiedBy() != null) {
            Optional<UserBasicDto> createdByUser = userService.getById(userDto.createdBy());
            Optional<UserBasicDto> modifiedByUser = userService.getById(userDto.lastModifiedBy());
            createdByUser.ifPresent(userBasicDto -> model.addAttribute("createdByUser", userBasicDto));
            modifiedByUser.ifPresent(userBasicDto -> model.addAttribute("modifiedByUser", userBasicDto));
        }
        return "/system/user/crud/detail";
    }
}
