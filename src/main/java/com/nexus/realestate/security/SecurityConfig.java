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
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(java.util.List.of("*"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    return corsConfiguration;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Στατικά αρχεία Frontend — ελεύθερη πρόσβαση
                        .requestMatchers(
                                "/", "/*.html",
                                "/css/**", "/js/**", "/images/**",
                                "/admin/**",
                                "/favicon.ico"
                        ).permitAll()
                        // API endpoints — ελεύθερη πρόσβαση
                        .requestMatchers("/api/auth/**", "/api/properties", "/api/properties/search", "/api/heatmap", "/error").permitAll()
                        // ΔΙΟΡΘΩΣΗ: Ανοίγουμε την προβολή (details) σε όλους
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/properties/{id}").permitAll()
                        // ΔΙΟΡΘΩΣΗ: Επιτρέπουμε τα προτεινόμενα στους αγοραστές
                        .requestMatchers("/api/properties/recommended").hasRole("BUYER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/preferences/**").hasRole("BUYER")
                        .requestMatchers("/api/properties/**").hasAnyRole("OWNER", "ADMIN")
                        .anyRequest().authenticated()
                );

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