package com.ong.api_backend.controller;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FaleConoscoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/fale_conosco")
public class FaleConoscoController {
    private static final Logger logger = LoggerFactory.getLogger(FaleConoscoController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private FaleConoscoService faleConoscoService;

    public FaleConoscoController(FaleConoscoService faleConoscoService, EmailService emailService) {
        this.faleConoscoService = faleConoscoService;
        this.emailService = emailService;
        logger.debug("FaleConoscoController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(@RequestBody @Valid FaleConosco faleConosco) {
        logger.info("Received request to save FaleConosco message");
        try {
            faleConoscoService.saveAllService(faleConosco);
            logger.info("FaleConosco message saved successfully");
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        } catch (Exception e) {
            logger.error("Error saving FaleConosco message: {}", e.getMessage(), e);
            throw new DadosInvalidosException("Dados inválidos ou faltantes");
        }
    }

    @GetMapping

    public ResponseEntity<List<FaleConosco>> listarTodos() {
        List<FaleConosco> lista = faleConoscoService.listarTodosFale();
        return ResponseEntity.ok(lista);
    }
}