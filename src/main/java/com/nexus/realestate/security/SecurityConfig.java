package com.nexus.realestate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Απενεργοποίηση CSRF για REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless συνεδρίες
                .authorizeHttpRequests(auth -> auth
                        // Δημόσια endpoints
                        .requestMatchers("/api/auth/**", "/api/properties", "/api/heatmap").permitAll()
                        // Κανόνες ανά ρόλο βάσει του οδηγού
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/preferences/**").hasRole("BUYER")
                        .requestMatchers("/api/properties/**").hasAnyRole("OWNER", "ADMIN")
                        .anyRequest().authenticated()
                );

        // Τοποθέτηση του JWT φίλτρου πριν από το βασικό φίλτρο του Spring Security
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt για ασφαλή κρυπτογράφηση κωδικών
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}