package com.duberlyguarnizo.plh.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
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

    @PostMapping(value = "/user/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> changeMyUserPassword(@RequestBody Map<String, String> pwds) {
        boolean result;
        log.warn("received data: ");
        log.warn(pwds.toString());
        if (userService.verifyCurrentUserPassword(pwds.get("currentPassword"))) {
            result = userService.changeCurrentUserPassword(pwds.get("newPassword"));
            if (result) {
                log.warn("User password changed");
                return new ResponseEntity<>(Map.of("result", "PASSWORD_CHANGED"), HttpStatus.OK);
            } else {
                log.warn("User password not changed due to error");
                return new ResponseEntity<>(Map.of("result", "ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Current password is incorrect");
            return new ResponseEntity<>(Map.of("result", "BAD_PASSWORD"), HttpStatus.OK);
        }

    }

    @PostMapping(value = "/admin/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> changeOtherUserPassword(@RequestBody Map<String, String> pwds) {
        boolean result;
        log.warn("received data: ");
        log.warn(pwds.toString());
        String newPassword = pwds.get("newPassword");
        String userName = pwds.get("username");
        if (newPassword != null && !newPassword.isEmpty()) {
            result = userService.changeOtherUserPassword(userName, newPassword);
            if (result) {
                log.warn("Other user password changed");
                return new ResponseEntity<>(Map.of("result", "PASSWORD_CHANGED"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("result", "ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Other user password not changed due to error");
            return new ResponseEntity<>(Map.of("result", "ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
