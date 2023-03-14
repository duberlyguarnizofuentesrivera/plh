package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.util.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")

public class UserAPIController {
    private final UserService userService;
    private final ImageService imageService;

    public UserAPIController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        Optional<UserDto> user = userService.getByUsername(username);
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/current")
    public ResponseEntity<UserBasicDto> getCurrentUser() {
        Optional<UserBasicDto> user = userService.getCurrentUser();
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    //TODO: Refactor frontend to accept the new booleans returned by methods
    @GetMapping
    public ResponseEntity<List<UserBasicDto>> getUserListWithFilters(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(defaultValue = "firstName") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest paging = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        return ResponseEntity.ok(userService
                .getWithFilters(search, status, role, paging)
                .getContent());
    }

    @PostMapping(value = "/change-password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changeMyUserPassword(@RequestBody Map<String, String> pwds) {
        boolean result;
        log.warn("received data: ");
        log.warn(pwds.toString());
        if (userService.verifyCurrentUserPassword(pwds.get("currentPassword"))) {
            result = userService.changeCurrentUserPassword(pwds.get("newPassword"));
            if (result) {
                log.warn("User password changed");
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                log.warn("User password not changed due to error");
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Current password is incorrect");
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(value = "/change-any-password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changeOtherUserPassword(@RequestBody Map<String, String> pwds) {
        boolean result;
        log.warn("received data: ");
        log.warn(pwds.toString());
        String newPassword = pwds.get("newPassword");
        String userName = pwds.get("username");
        if (newPassword != null && !newPassword.isEmpty()) {
            result = userService.changeOtherUserPassword(userName, newPassword);
            if (result) {
                log.warn("UserAPIController: changeOtherUserPassword(): Other user password changed");
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("UserAPIController: changeOtherUserPassword(): Other user password not changed due to error in parameters");
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    //TODO: Refactor to use only HTTP responses and not maps.
    public ResponseEntity<Boolean> deleteUser(@PathVariable String username) {
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (currentUser.get().username().equals(username))) {
            //You are trying to delete your own user!!!
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.FORBIDDEN);
        }

        boolean result = userService.deleteByUserName(username);

        if (result) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Boolean> changeUserStatus(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (currentUser.get().username().equals(username))) {
            //You are trying to change status of your own user!!!
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.FORBIDDEN);
        }
        boolean result = userService.setStatus(username);
        if (result) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        log.warn("UploadProfilePicture");
        log.warn("received username= " + username);
        log.warn("received file: " + file.toString());
        boolean result = imageService.saveProfilePicture(file, username);
        if (!result) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
}
