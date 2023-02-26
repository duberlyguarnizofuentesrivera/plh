package com.duberlyguarnizo.plh.generators;

import com.duberlyguarnizo.plh.user.User;
import com.duberlyguarnizo.plh.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CurrentUserAuditorAware implements AuditorAware<User> {
    private final UserRepository repository;

    public CurrentUserAuditorAware(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @NonNull
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object result = authentication.getPrincipal();
        log.warn("AUDITOR AWARE: Current principal is: " + result);
        if (result instanceof String) {
            return repository.findByUsername((String) authentication.getPrincipal());
        } else {
            org.springframework.security.core.userdetails.User userDetail =
                    (org.springframework.security.core.userdetails.User) result;
            return repository.findByUsername(userDetail.getUsername());
        }

    }
}
