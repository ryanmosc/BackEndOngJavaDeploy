package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.service.FormularioDoacaoMensalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doacao_mensal")
public class FormularioDoacaoMensalController {
    @Autowired
    private FormularioDoacaoMensalService formularioDoacaoMensalService;

    @PostMapping
    public ResponseEntity<String> saveAll (@RequestBody @Valid FormularioDoacaoMensal formularioDoacaoMensal){
        try{
            formularioDoacaoMensalService.saveAllFormularioDoacaoMensalService(formularioDoacaoMensal);
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }
}
