package com.ong.api_backend.controller;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.service.FaleConoscoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@RequestMapping("/api/fale_conosco")
public class FaleConoscoController {
    @Autowired
    private  FaleConoscoService faleConoscoService;

    @PostMapping
    public ResponseEntity<String> saveAll(@RequestBody @Valid FaleConosco faleConosco){
        try {
            faleConoscoService.saveAllService(faleConosco);
            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");
        }
        catch (Exception e){
            throw new DadosInvalidosException("Dados invalidos ou faltantes");
        }
    }

}
