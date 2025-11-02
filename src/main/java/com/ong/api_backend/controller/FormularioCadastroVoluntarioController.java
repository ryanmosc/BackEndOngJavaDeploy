package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.service.FormularioCadastroVoluntarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seja_voluntario")
@CrossOrigin(origins = "*")
public class FormularioCadastroVoluntarioController {
    private static final Logger logger = LoggerFactory.getLogger(FormularioCadastroVoluntarioController.class);

    @Autowired
    private FormularioCadastroVoluntarioService formularioCadastroVoluntarioService;

    public FormularioCadastroVoluntarioController(FormularioCadastroVoluntarioService formularioCadastroVoluntarioService) {
        this.formularioCadastroVoluntarioService = formularioCadastroVoluntarioService;
        logger.debug("FormularioCadastroVoluntarioController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(@RequestBody @Valid FormularioCadastroVoluntario formularioCadastroVoluntario) {
        logger.info("Received request to save FormularioCadastroVoluntario");
        try {
            formularioCadastroVoluntarioService.saveAllFormularioCadastroVoluntariosService(formularioCadastroVoluntario);
            logger.info("FormularioCadastroVoluntario saved successfully");
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        } catch (Exception e) {
            logger.error("Error saving FormularioCadastroVoluntario: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioCadastroVoluntario>> getAll(){
        logger.info("Received request to list all Seja voluntario");
        List<FormularioCadastroVoluntario> forms = formularioCadastroVoluntarioService.listarTodos();
        return ResponseEntity.ok(forms);
    }
}