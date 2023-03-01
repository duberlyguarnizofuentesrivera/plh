package com.duberlyguarnizo.plh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PlhApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlhApplication.class, args);
    }

}
