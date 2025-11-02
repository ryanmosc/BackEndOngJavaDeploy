package com.ong.api_backend.infra.security;

import com.ong.api_backend.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired private com.ong.api_backend.infra.security.TokenService tokenService;
    @Autowired private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String login = tokenService.validateToken(token);

            if (login == null || login.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido");
                return;
            }

            UserDetails user = userRepository.findByLogin(login);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usuário não encontrado");
                return;
            }

            DecodedJWT decodedJWT = JWT.decode(token);
            String role = decodedJWT.getClaim("role").asString();

            var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
            );

            logger.info("Autenticado: {} com role: {}", login, role);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Erro JWT: ", e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Erro na autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

