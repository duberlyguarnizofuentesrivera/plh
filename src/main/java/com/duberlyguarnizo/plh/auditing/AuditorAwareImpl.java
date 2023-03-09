package com.duberlyguarnizo.plh.auditing;

import com.duberlyguarnizo.plh.user.User;
import com.duberlyguarnizo.plh.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final UserRepository repository;

    @Autowired
    public AuditorAwareImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        } else {
            User principal =
                    (User) authentication
                            .getPrincipal();
            String username = principal.getUsername();
            Optional<User> currentUser = repository.findByUsername(username);
            return currentUser.map(User::getId);
        }
    }
}
