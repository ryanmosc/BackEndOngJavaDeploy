package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.service.FaleConoscoService;
import com.ong.api_backend.service.FormularioCadastroVoluntarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seja_voluntario")
@CrossOrigin(origins = "http://localhost:5500")
public class FormularioCadastroVoluntarioController {
    @Autowired
    private FormularioCadastroVoluntarioService formularioCadastroVoluntarioService;

    @PostMapping
    public ResponseEntity<String> saveAll(@RequestBody @Valid FormularioCadastroVoluntario formularioCadastroVoluntario){
        try{
            formularioCadastroVoluntarioService.saveAllFormularioCadastroVoluntariosService(formularioCadastroVoluntario);
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Erro ao enviar mensagem\"}");

        }
    }
}
