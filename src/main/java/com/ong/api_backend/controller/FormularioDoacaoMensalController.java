package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FormularioDoacaoMensalService;
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
@RequestMapping("/api/doacao_mensal")
public class FormularioDoacaoMensalController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioDoacaoMensalController.class);

    @Autowired
    private FormularioDoacaoMensalService formularioDoacaoMensalService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IpUtil ipUtil;

    public FormularioDoacaoMensalController(
            FormularioDoacaoMensalService formularioDoacaoMensalService,
            EmailService emailService,
            IpUtil ipUtil) {

        this.formularioDoacaoMensalService =
                formularioDoacaoMensalService;

        this.emailService = emailService;
        this.ipUtil = ipUtil;

        logger.debug("FormularioDoacaoMensalController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioDoacaoMensal formularioDoacaoMensal,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to save FormularioDoacaoMensal",
                ip
        );

        try {

            logger.info(
                    "[IP: {}] Monthly donation form received | Nome: {} | Email: {}",
                    ip,
                    formularioDoacaoMensal.getNomeCompleto(),
                    formularioDoacaoMensal.getEmail()
            );

            formularioDoacaoMensalService
                    .saveAllFormularioDoacaoMensalService(
                            formularioDoacaoMensal
                    );

            logger.info(
                    "[IP: {}] FormularioDoacaoMensal saved successfully",
                    ip
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error saving FormularioDoacaoMensal: {}",
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
    public ResponseEntity<List<FormularioDoacaoMensal>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to list all monthly donations",
                ip
        );

        List<FormularioDoacaoMensal> lista =
                formularioDoacaoMensalService.listarTodosMensal();

        logger.debug(
                "[IP: {}] Returning {} monthly donation registrations",
                ip,
                lista.size()
        );

        return ResponseEntity.ok(lista);
    }
}