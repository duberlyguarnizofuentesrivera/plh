package com.duberlyguarnizo.plh.auditing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditingConf {
    private final AuditorAwareImpl implementation;

    @Autowired
    public AuditingConf(AuditorAwareImpl implementation) {
        this.implementation = implementation;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return implementation;
    }
}
