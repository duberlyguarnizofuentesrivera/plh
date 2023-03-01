package com.duberlyguarnizo.plh.auditing;

import com.duberlyguarnizo.plh.user.User;
import com.duberlyguarnizo.plh.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
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
        User principal =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        String username = principal.getUsername();
        Optional<User> currentUser = repository.findByUsername(username);
        return currentUser.map(User::getId);
    }
}
