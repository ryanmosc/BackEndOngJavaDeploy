package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FormularioCadastroVoluntarioService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seja_voluntario")
public class FormularioCadastroVoluntarioController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioCadastroVoluntarioController.class);

    @Autowired
    private FormularioCadastroVoluntarioService formularioCadastroVoluntarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IpUtil ipUtil;

    public FormularioCadastroVoluntarioController(
            FormularioCadastroVoluntarioService formularioCadastroVoluntarioService,
            EmailService emailService,
            IpUtil ipUtil) {

        this.formularioCadastroVoluntarioService =
                formularioCadastroVoluntarioService;

        this.emailService = emailService;
        this.ipUtil = ipUtil;

        logger.debug("FormularioCadastroVoluntarioController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioCadastroVoluntario formularioCadastroVoluntario,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to save FormularioCadastroVoluntario",
                ip
        );

        try {

            logger.info(
                    "[IP: {}] Volunteer form data received | Nome: {} | Email: {}",
                    ip,
                    formularioCadastroVoluntario.getNome_completo(),
                    formularioCadastroVoluntario.getE_mail()
            );

            formularioCadastroVoluntarioService
                    .saveAllFormularioCadastroVoluntariosService(
                            formularioCadastroVoluntario
                    );

            logger.info(
                    "[IP: {}] FormularioCadastroVoluntario saved successfully",
                    ip
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error saving FormularioCadastroVoluntario: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioCadastroVoluntario>> getAll(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to list all volunteer registrations",
                ip
        );

        List<FormularioCadastroVoluntario> lista =
                formularioCadastroVoluntarioService.listarTodos();

        logger.debug(
                "[IP: {}] Returning {} volunteer registrations",
                ip,
                lista.size()
        );

        return ResponseEntity.ok(lista);
    }
}