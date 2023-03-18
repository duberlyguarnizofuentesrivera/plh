package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.util.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserAPIController {

    private final UserService userService;
    private final ImageService imageService;
    private final PagedResourcesAssembler<UserBasicDto> pagedResourcesAssembler;

    public UserAPIController(UserService userService, ImageService imageService, PagedResourcesAssembler<UserBasicDto> pagedResourcesAssembler) {
        this.userService = userService;
        this.imageService = imageService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
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
                    .withRel("delete"));
            result.add(linkTo(methodOn(UserAPIController.class)
                    .updateUser(null))
                    .withRel("patch"));
            result.add(linkTo(methodOn(UserAPIController.class)
                    .uploadProfilePicture(null, result.username))
                    .withRel("uploadProfilePicture"));

            result.add(linkTo(UserAPIController.class).slash("?search=" + result.getLastName())
                    .withRel("search"));
        }
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/current")
    public ResponseEntity<UserBasicDto> getCurrentUser() {
        Optional<UserBasicDto> user = userService.getCurrentUser();
        if (user.isPresent()) {
            UserBasicDto result = user.get();
            result.add(linkTo(methodOn(UserAPIController.class)
                    .getUser(result.getUsername()))
                    .withSelfRel());
            result.add(linkTo(methodOn(UserAPIController.class)
                    .deleteUser(result.getUsername()))
                    .withRel("delete"));
            result.add(linkTo(methodOn(UserAPIController.class)
                    .updateUser(null))
                    .withRel("patch"));
            result.add(linkTo(methodOn(UserAPIController.class)
                    .uploadProfilePicture(null, result.username))
                    .withRel("uploadProfilePicture"));
            result.add(linkTo(UserAPIController.class).slash("?search=" + result.getLastName())
                    .withRel("search"));
        }
        return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @GetMapping
//    @Cacheable(cacheNames = "listOfUsers")
    public ResponseEntity<PagedModel<EntityModel<UserBasicDto>>> getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(defaultValue = "firstName") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        //Controllers have the task to rest 1 to the page number
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        try {
            Page<UserBasicDto> result = userService
                    .getWithFilters(search, status, role, paging).map(user -> user
                            .add(linkTo(methodOn(UserAPIController.class)
                                    .getUser(user.getUsername()))
                                    .withSelfRel())
                            //TODO: implement search ticket by user and replace this
                            .add(linkTo(UserAPIController.class)
                                    .slash("?search=" + user.getLastName())
                                    .withRel("search")));
            var pagedModel = pagedResourcesAssembler.toModel(result);
            if (result.isEmpty()) {
                //TODO: implement message indicator for this error
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(pagedModel, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping
    public ResponseEntity<Boolean> createUser(@RequestBody UserDetailDto dto) {
        var result = userService.save(dto);
        return result ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
        if (data == null) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
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
