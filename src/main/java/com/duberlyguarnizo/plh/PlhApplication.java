package com.duberlyguarnizo.plh;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.user.UserDetailDto;
import com.duberlyguarnizo.plh.user.UserDto;
import com.duberlyguarnizo.plh.user.UserService;
import com.duberlyguarnizo.plh.util.ImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PlhApplication implements CommandLineRunner {

    @Resource
    private final ImageService imageService;
    @Resource
    private final UserService userService;

    public PlhApplication(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PlhApplication.class, args);
    }

    @Override
    public void run(String... args) {
        SimpleCommandLinePropertySource propertySource = new SimpleCommandLinePropertySource(args);
        imageService.init();
        if (propertySource.containsProperty("create-admin") &&
                propertySource.containsProperty("admin-password")) {
            String newAdminPassword = propertySource.getProperty("admin-password");
            Optional<UserDto> firstUser = userService.getByUsername("main_admin");
            if (firstUser.isEmpty()) {
                if (newAdminPassword == null) {
                    log.warn("There was an attempt to create a first admin user, but the password was null!");
                } else {
                    if (newAdminPassword.isEmpty()) {
                        log.warn("There was an attempt to create a first admin user, but the password was invalid!");
                    } else {
                        UserDetailDto newUser = UserDetailDto.builder()
                                .firstName("Duberly")
                                .lastName("Guarnizo")
                                .idNumber("922618630")
                                .email("duberlygfr@gmail.com")
                                .phone("922618630")
                                .role(UserRole.ADMIN)
                                .status(UserStatus.ACTIVE)
                                .username("admin")
                                .password(newAdminPassword)
                                .build();
                        userService.save(newUser);
                    }
                }
            }
        } else {
            log.warn("There was an unsuccessful attempt to create an existing first admin user!");
        }
    }

}
