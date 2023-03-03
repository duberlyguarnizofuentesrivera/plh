package com.duberlyguarnizo.plh;

import com.duberlyguarnizo.plh.util.ImageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PlhApplication implements CommandLineRunner {
    @Resource
    private final ImageService imageService;

    public PlhApplication(ImageService imageService) {
        this.imageService = imageService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PlhApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        imageService.init();
    }
}
