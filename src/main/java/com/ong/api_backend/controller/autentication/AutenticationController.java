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
    import java.util.HashMap;

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
            long inicio = System.currentTimeMillis();

            HashMap<String, Object> payload = construirPayloadBase(
                    ip, data.login(), "login", "/auth/login", request
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

                payload.put("Status", "Sucesso");
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.info("[IP: {}] Login realizado com sucesso para usuário: {}", ip, data.login());
                logsService.salvarLog(payload);

                return ResponseEntity.ok(new LoginResponseDTO(token));

            } catch (Exception e) {

                payload.put("Status", "Falha");
                payload.put("Erro", e.getMessage());
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.error("[IP: {}] Falha no login do usuário {}: {}", ip, data.login(), e.getMessage(), e);
                logsService.salvarLog(payload);

                return ResponseEntity.badRequest().build();
            }
        }

        @PostMapping("/register")
        public ResponseEntity<?> register(
                @RequestBody @Valid RegisterDTO data,
                HttpServletRequest request) {

            String ip = IpUtil.getClientIp(request);
            long inicio = System.currentTimeMillis();

            HashMap<String, Object> payload = construirPayloadBase(
                    ip, data.login(), "register", "/auth/register", request
            );

            try {
                if (userRepository.findByLogin(data.login()) != null) {

                    payload.put("Status", "Falha");
                    payload.put("Erro", "Usuário já existente");
                    payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                    logger.warn("[IP: {}] Usuário já existente: {}", ip, data.login());
                    logsService.salvarLog(payload);

                    return ResponseEntity.badRequest().body("Usuário já existente");
                }

                String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
                User newUser = new User(data.login(), encryptedPassword, data.role());
                userRepository.save(newUser);

                payload.put("Status", "Sucesso");
                payload.put("Role", data.role());
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.info("[IP: {}] Usuário registrado com sucesso: {}", ip, data.login());
                logsService.salvarLog(payload);

                return ResponseEntity.ok("Usuário registrado com sucesso");

            } catch (Exception e) {

                payload.put("Status", "Falha");
                payload.put("Erro", e.getMessage());
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.error("[IP: {}] Erro ao registrar usuário {}: {}", ip, data.login(), e.getMessage(), e);
                logsService.salvarLog(payload);

                return ResponseEntity.internalServerError().body("Erro ao registrar usuário");
            }
        }

        // Monta o payload base reutilizável
        private HashMap<String, Object> construirPayloadBase(
                String ip,
                String usuario,
                String acao,
                String endpoint,
                HttpServletRequest request) {

            HashMap<String, Object> payload = new HashMap<>();
            payload.put("Ip", ip);
            payload.put("User", usuario);
            payload.put("Action", acao);
            payload.put("End-Point", endpoint);
            payload.put("Method", request.getMethod());
            payload.put("User-Agent", request.getHeader("User-Agent"));
            payload.put("Hour", LocalDateTime.now().toString());
            return payload;
        }
        }