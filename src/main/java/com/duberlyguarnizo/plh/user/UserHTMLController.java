package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("users")
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
    @PostMapping("/create")
    public boolean createUser(@RequestBody UserRegisterDto userRegister) {
        return userService.save(userRegister);
    }

    @PostMapping("/update")
    public boolean updateUser(@RequestBody UserRegisterDto userRegister) {
        return userService.update(userRegister, true);
    }

    @GetMapping("/delete/{username}")
    public boolean deleteUser(@PathVariable("username") String username) {
        return userService.deleteByUserName(username);
    }

    @GetMapping("/all")
    public Page<UserBasicDto> getAllUser(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        Page<UserBasicDto> result = userService.findAll(page, size);
        result.getTotalPages();
        return result;
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

    @GetMapping("/by-username/{username}")
    public UserDto getUsersByUserName(@PathVariable("username") String username) {
        return userService.getByUsername(username).orElse(null);
    }
}
