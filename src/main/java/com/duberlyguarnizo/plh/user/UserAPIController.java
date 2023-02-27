package com.duberlyguarnizo.plh.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserAPIController {
    private final UserService userService;

    public UserAPIController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/list")
    public ResponseEntity<List<UserBasicDto>> getUserListWithFilters(@RequestParam(defaultValue = "all") String status,
                                                                     @RequestParam(defaultValue = "all") String role,
                                                                     @RequestParam(defaultValue = "") String search,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService
                .findWithFilters(status, role, search, page, size)
                .getContent());
    }
}
