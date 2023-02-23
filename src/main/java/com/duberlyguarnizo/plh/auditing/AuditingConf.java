package com.duberlyguarnizo.plh.auditing;

import com.duberlyguarnizo.plh.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditingConf {
    private final UserRepository repository;

    @Autowired
    public AuditingConf(UserRepository repository) {
        this.repository = repository;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new AuditorAwareImpl(repository);
    }
}
