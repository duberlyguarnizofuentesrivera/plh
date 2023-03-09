package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.util.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")

public class UserAPIController {
    private static final String ERROR_CUSTOM_STATUS = "ERROR";
    private static final String RESULT_TEXT = "result";
    private final UserService userService;
    private final ImageService imageService;

    public UserAPIController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    //TODO: Change endpoint names to comply with standards
    @GetMapping("/list")
    public ResponseEntity<List<UserBasicDto>> getUserListWithFilters(@RequestParam(defaultValue = "all") String status,
                                                                     @RequestParam(defaultValue = "all") String role,
                                                                     @RequestParam(defaultValue = "") String search,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService
                .getWithFilters(status, role, search, page, size)
                .getContent());
    }

    @PostMapping(value = "/change-password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> changeMyUserPassword(@RequestBody Map<String, String> pwds) {
        boolean result;
        log.warn("received data: ");
        log.warn(pwds.toString());
        if (userService.verifyCurrentUserPassword(pwds.get("currentPassword"))) {
            result = userService.changeCurrentUserPassword(pwds.get("newPassword"));
            if (result) {
                log.warn("User password changed");
                return new ResponseEntity<>(Map.of(RESULT_TEXT, "PASSWORD_CHANGED"), HttpStatus.OK);
            } else {
                log.warn("User password not changed due to error");
                return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Current password is incorrect");
            return new ResponseEntity<>(Map.of(RESULT_TEXT, "BAD_PASSWORD"), HttpStatus.OK);
        }

    }

    @PostMapping(value = "/change-any-password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
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
                return new ResponseEntity<>(Map.of(RESULT_TEXT, "PASSWORD_CHANGED"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Other user password not changed due to error");
            return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (currentUser.get().username().equals(username))) {
            //You are trying to delete your own user!!!
            return new ResponseEntity<>(Map.of(RESULT_TEXT, "CANNOT_DELETE_SAME_USER"), HttpStatus.OK);
        }

        boolean result = userService.deleteByUserName(username);

        if (result) {
            return new ResponseEntity<>(Map.of(RESULT_TEXT, "USER_DELETED"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.OK);
        }
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> changeUserStatus(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (currentUser.get().username().equals(username))) {
            //You are trying to change status of your own user!!!
            return new ResponseEntity<>(Map.of(RESULT_TEXT, "CANNOT_DELETE_SAME_USER"), HttpStatus.OK);
        }
        boolean result = userService.setStatus(username);
        if (result) {
            return new ResponseEntity<>(Map.of(RESULT_TEXT, "STATUS_CHANGED"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        log.warn("UploadProfilePicture");
        log.warn("received username= " + username);
        log.warn("received file: " + file.toString());
        boolean result = imageService.saveProfilePicture(file, username);
        if (!result) {
            return new ResponseEntity<>(Map.of(RESULT_TEXT, ERROR_CUSTOM_STATUS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Map.of(RESULT_TEXT, "IMAGE_UPLOADED"), HttpStatus.OK);
    }
}
