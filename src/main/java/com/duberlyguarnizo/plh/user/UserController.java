package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //get current user
    @GetMapping("/logged-user")
    public Principal loggedUser(Principal user) {
        return user;
    }

    //CRUD methods
    @PostMapping("/create")
    public User createUser(@RequestBody User systemUser) {
        return userRepository.save(systemUser);
    }

    @PutMapping("/update")
    public User updateUser(@RequestBody User systemUser) {
        return userRepository.save(systemUser);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<User> getAllUser(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        Page<User> result = userRepository.findAll(PageRequest.of(page, size));
        result.getTotalPages();
        return result;
    }

    @GetMapping("/by-role/{role}")
    public Page<User> getAllUsersByRole(@PathVariable("role") String role, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            UserRole userRole = UserRole.valueOf(role);
            return userRepository.findByRole(userRole, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @GetMapping("/by-status/{status}")
    public Page<User> getAllUsersByUserStatus(@PathVariable("status") String status, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status);
            return userRepository.findByStatus(userStatus, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @GetMapping("/by-username/{username}")
    public User getUsersByUserName(@PathVariable("username") String username) {
        List<User> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
