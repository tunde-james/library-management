package com.example.librarymanagement.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.librarymanagement.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;
    private final UserDetailsService userDetailsService;


    public SecurityConfig(JwtAuthenticationFilter authenticationFilter,
            UserDetailsService userDetailsService) {
        this.authenticationFilter = authenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
}
