package com.duberlyguarnizo.plh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String ADMIN_AUTHORITY = "ADMIN";
    private static final String SUPERVISOR_AUTHORITY = "SUPERVISOR";
    private static final String DISTPATCHER_AUTHORITY = "DISPATCHER";
    private static final String TRANSPORTER_AUTHORITY = "TRANSPORTER";

    private final String rememberKey;

    @Autowired
    public SecurityConfiguration(@Value("${custom.properties.remember.key}") String rememberKey) {
        this.rememberKey = rememberKey;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.TRACE).denyAll()
                .requestMatchers("/admin/").hasAnyAuthority(ADMIN_AUTHORITY, SUPERVISOR_AUTHORITY)
                .requestMatchers("/system/transporter").hasAnyAuthority(ADMIN_AUTHORITY, SUPERVISOR_AUTHORITY, TRANSPORTER_AUTHORITY)
                .requestMatchers("/system/dispatcher").hasAnyAuthority(ADMIN_AUTHORITY, SUPERVISOR_AUTHORITY, DISTPATCHER_AUTHORITY)
                .requestMatchers("/system/users/crud/**").hasAnyAuthority(ADMIN_AUTHORITY, SUPERVISOR_AUTHORITY)
                .requestMatchers("/system/**", "/auth/**", "/system-assets/**", "/uploads/profilePics/**").authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/system", true)
                .failureUrl("/login?error=true")
                //.failureHandler(authenticationFailureHandler()) //to audit login attempts
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                //.logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .rememberMe()
                .alwaysRemember(true)
                .rememberMeParameter("remember") //unnecessary now
                .key(rememberKey);

        return http.build();
    }

    @Bean
    PasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }
}
