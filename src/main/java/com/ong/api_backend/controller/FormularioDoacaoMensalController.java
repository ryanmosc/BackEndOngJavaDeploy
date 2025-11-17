package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FormularioDoacaoMensalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doacao_mensal")
public class FormularioDoacaoMensalController {
    private static final Logger logger = LoggerFactory.getLogger(FormularioDoacaoMensalController.class);

    @Autowired
    private FormularioDoacaoMensalService formularioDoacaoMensalService;

    @Autowired
    private EmailService emailService;

    public FormularioDoacaoMensalController(FormularioDoacaoMensalService formularioDoacaoMensalService, EmailService emailService) {
        this.formularioDoacaoMensalService = formularioDoacaoMensalService;
        this.emailService = emailService;
        logger.debug("FormularioDoacaoMensalController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(@RequestBody @Valid FormularioDoacaoMensal formularioDoacaoMensal) {
        logger.info("Received request to save FormularioDoacaoMensal");
        try {
            formularioDoacaoMensalService.saveAllFormularioDoacaoMensalService(formularioDoacaoMensal);
            logger.info("FormularioDoacaoMensal saved successfully");
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        } catch (Exception e) {
            logger.error("Error saving FormularioDoacaoMensal: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping

    public ResponseEntity<List<FormularioDoacaoMensal>> listarTodos() {
        return ResponseEntity.ok(formularioDoacaoMensalService.listarTodosMensal());
    }
}