package com.ipin.identity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableMethodSecurity
public class SecurityConfig {

    String[] PUBLIC_ENDPOINTS = {"/user", "/auth/login", "/auth/introspect", "/auth/logout", "/auth/refresh"};

    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> 
            auth.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
            .anyRequest().authenticated())
        .csrf(csrf -> csrf.disable())
        .oauth2ResourceServer(oauth2 -> 
            oauth2.jwt(jwtConfigurer -> 
                jwtConfigurer.decoder(customJwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        return authenticationConverter;
    }
}
