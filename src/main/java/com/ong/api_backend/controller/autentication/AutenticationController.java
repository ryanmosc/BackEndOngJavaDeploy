package com.ong.api_backend.controller.autentication;

import com.ong.api_backend.infra.security.TokenService;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.model.user.AutenticationDTO;
import com.ong.api_backend.model.user.LoginResponseDTO;
import com.ong.api_backend.model.user.RegisterDTO;
import com.ong.api_backend.model.user.User;
import com.ong.api_backend.repository.UserRepository;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticationController {

    private static final Logger logger =
            LoggerFactory.getLogger(AutenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final LogsService logsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid AutenticationDTO data,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Tentativa de login para usuário: {}",
                ip,
                data.login()
        );

        salvarLog(
                ip,
                "Tentativa de login para usuário: " + data.login()
        );

        try {

            UsernamePasswordAuthenticationToken userAuth =
                    new UsernamePasswordAuthenticationToken(
                            data.login(),
                            data.password()
                    );

            Authentication authentication =
                    authenticationManager.authenticate(userAuth);

            String token = tokenService.generateToken(
                    (User) authentication.getPrincipal()
            );

            logger.info(
                    "[IP: {}] Login realizado com sucesso para usuário: {}",
                    ip,
                    data.login()
            );

            salvarLog(
                    ip,
                    "Login realizado com sucesso para usuário: "
                            + data.login()
            );

            return ResponseEntity.ok(
                    new LoginResponseDTO(token)
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Falha no login do usuário {}: {}",
                    ip,
                    data.login(),
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Falha no login do usuário "
                            + data.login()
                            + ": "
                            + e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterDTO data,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Tentativa de registro de usuário: {}",
                ip,
                data.login()
        );

        salvarLog(
                ip,
                "Tentativa de registro de usuário: " + data.login()
        );

        try {

            if (userRepository.findByLogin(data.login()) != null) {

                logger.warn(
                        "[IP: {}] Usuário já existente: {}",
                        ip,
                        data.login()
                );

                salvarLog(
                        ip,
                        "Falha no registro: usuário já existente -> "
                                + data.login()
                );

                return ResponseEntity
                        .badRequest()
                        .body("Usuário já existente");
            }

            String encryptedPassword =
                    new BCryptPasswordEncoder()
                            .encode(data.password());

            User newUser = new User(
                    data.login(),
                    encryptedPassword,
                    data.role()
            );

            userRepository.save(newUser);

            logger.info(
                    "[IP: {}] Usuário registrado com sucesso: {}",
                    ip,
                    data.login()
            );

            salvarLog(
                    ip,
                    "Usuário registrado com sucesso: "
                            + data.login()
            );

            return ResponseEntity.ok(
                    "Usuário registrado com sucesso"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao registrar usuário {}: {}",
                    ip,
                    data.login(),
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao registrar usuário "
                            + data.login()
                            + ": "
                            + e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body("Erro ao registrar usuário");
        }
    }

    private void salvarLog(String ip, String mensagem) {

        Logs log = new Logs();

        log.setIp_requisicao(ip);
        log.setLog(mensagem);
        log.setLocalDateTime(LocalDateTime.now());

        logsService.salvarLog(log);
    }
}