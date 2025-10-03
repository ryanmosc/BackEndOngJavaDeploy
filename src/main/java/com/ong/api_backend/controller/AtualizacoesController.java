package com.ong.api_backend.controller;

import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.model.Evento;
import com.ong.api_backend.service.AualizacoesService;
import com.ong.api_backend.service.EventoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gerencia/atualizacoes")
public class AtualizacoesController {
    private final AualizacoesService service;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AtualizacoesController(AualizacoesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Atualizacoes> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {
        Atualizacoes atualizacoes = new Atualizacoes();
        atualizacoes.setTexto(texto);
        Atualizacoes saved = service.salvar(atualizacoes, imagem);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atualizacoes> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {
        Atualizacoes updated = service.atualizar(Math.toIntExact(id), texto, imagem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.ok("Atualização deletada com sucesso");
    }

    @GetMapping
    public ResponseEntity<List<Atualizacoes>> listarTodos() {
        List<Atualizacoes> eventos = service.listarTodos();
        return ResponseEntity.ok(eventos);
    }
}
