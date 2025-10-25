package com.ong.api_backend.controller;

import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.service.AtualizacoesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AtualizacoesController.class);
    private final AtualizacoesService service;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AtualizacoesController(AtualizacoesService service) {
        this.service = service;
        logger.debug("AtualizacoesController initialized");
    }

    @PostMapping
    public ResponseEntity<Atualizacoes> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem) throws IOException {
        logger.info("Received request to add new Atualizacoes with texto: {}", texto);
        try {
            Atualizacoes atualizacoes = new Atualizacoes();
            atualizacoes.setTexto(texto);
            Atualizacoes saved = service.salvar(atualizacoes, imagem);
            logger.info("Atualizacoes saved successfully with ID: {}", saved.getId());
            return ResponseEntity.status(201).body(saved);
        } catch (IOException e) {
            logger.error("Error saving Atualizacoes: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atualizacoes> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem) throws IOException {
        logger.info("Received request to update Atualizacoes with ID: {}", id);
        try {
            Atualizacoes updated = service.atualizar((long) Math.toIntExact(id), texto, imagem);
            logger.info("Atualizacoes updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            logger.error("Error updating Atualizacoes with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) throws IOException {
        logger.info("Received request to delete Atualizacoes with ID: {}", id);
        try {
            service.deletar((long) Math.toIntExact(id));
            logger.info("Atualizacoes deleted successfully with ID: {}", id);
            return ResponseEntity.ok("Atualização deletada com sucesso");
        } catch (Exception e) {
            logger.error("Error deleting Atualizacoes with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Atualizacoes>> listarTodos() {
        logger.info("Received request to list all Atualizacoes");
        List<Atualizacoes> eventos = service.listarTodos();
        logger.debug("Returning {} Atualizacoes", eventos.size());
        return ResponseEntity.ok(eventos);
    }
}