package com.ong.api_backend.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/fale_conosco").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/seja_voluntario").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gerencia/eventos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gerencia/atualizacoes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/gerencia/eventos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/gerencia/eventos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/gerencia/eventos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/gerencia/atualizacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/gerencia/atualizacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/gerencia/atualizacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/fale_conosco").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/seja_voluntario").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/doacao_mensal").hasAuthority("ROLE_ADMIN")


                        .anyRequest().authenticated()
                )

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}