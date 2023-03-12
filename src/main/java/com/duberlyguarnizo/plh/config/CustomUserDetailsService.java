package com.duberlyguarnizo.plh.config;

import com.duberlyguarnizo.plh.user.User;
import com.duberlyguarnizo.plh.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Autowired
    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userSearch = repository.findByUsernameIgnoreCase(username);
        if (userSearch.isEmpty()) {
            throw new UsernameNotFoundException("CustomUserDetailsService: No user found with this username!");
        } else {
            return userSearch.get();
        }
    }
}
