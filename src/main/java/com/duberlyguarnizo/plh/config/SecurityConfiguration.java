package com.duberlyguarnizo.plh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/admin/").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/system/transporter").hasAnyRole("ADMIN", "SUPERVISOR", "TRANSPORTER")
                .requestMatchers("/system/dispatcher").hasAnyRole("ADMIN", "SUPERVISOR", "DISPATCHER")
                .requestMatchers("/system/**", "/auth/**", "/system-assets/**").authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/system/index", true)
                .failureUrl("/login?error=true")
                //.failureHandler(authenticationFailureHandler()) //to audit login attempts
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
                //.logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .rememberMe()
                .rememberMeParameter("remember");

        return http.build();
    }

    @Bean
    PasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }
}
