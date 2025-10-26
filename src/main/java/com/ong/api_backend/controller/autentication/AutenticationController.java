package com.ong.api_backend.controller.autentication;

import com.ong.api_backend.infra.security.TokenService;
import com.ong.api_backend.model.user.AutenticationDTO;
import com.ong.api_backend.model.user.LoginResponseDTO;
import com.ong.api_backend.model.user.RegisterDTO;
import com.ong.api_backend.model.user.User;
import com.ong.api_backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AutenticationDTO data) {
        UsernamePasswordAuthenticationToken userAuth =
                new UsernamePasswordAuthenticationToken(data.login(), data.password());

        Authentication authentication = authenticationManager.authenticate(userAuth);

        String token = tokenService.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().body("Usuário já existente");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        userRepository.save(newUser);

        return ResponseEntity.ok("Usuário registrado com sucesso");
    }
}
