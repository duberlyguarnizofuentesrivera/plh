package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")

public class UserAPIController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ImageService imageService;

    public UserAPIController(UserService userService, ImageService imageService,
                             UserRepository userRepository) {
        this.userService = userService;
        this.imageService = imageService;
        this.userRepository = userRepository;
    }

    @GetMapping("{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        Optional<UserDto> user = userService.getByUsername(username);
        if (user.isPresent()) {
            UserDto result = user.get();
            result.add(linkTo(methodOn(UserAPIController.class)
                    .getUser(username))
                    .withSelfRel());
            result.add(linkTo(methodOn(UserAPIController.class)
                    .deleteUser(username))
                    .withRel("DELETE"));
            result.add(linkTo(methodOn(UserAPIController.class)
                    .updateUser(null))
                    .withRel("PATCH"));

            result.add(linkTo(UserAPIController.class).slash("?search=" + username)
                    .withRel("search"));
        }
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/current")
    public ResponseEntity<UserBasicDto> getCurrentUser() {
        Optional<UserBasicDto> user = userService.getCurrentUser();
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @GetMapping
    public ResponseEntity<List<UserBasicDto>> getAll(
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


    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String username) {
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (currentUser.get().getUsername().equals(username))) {
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

    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Boolean> updateUser(@RequestBody UserDetailDto data) {
        var currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            UserRole currentRole = currentUser.get().getRole();
            boolean notAdminModifyingAdmin = (data.getRole() == UserRole.ADMIN && currentRole != UserRole.ADMIN);
            if (notAdminModifyingAdmin) {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.FORBIDDEN);
            }
        }
        boolean result = userService.update(data);
        if (result) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> uploadProfilePicture(@RequestParam("file") MultipartFile
                                                                file, @RequestParam("username") String username) {
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
