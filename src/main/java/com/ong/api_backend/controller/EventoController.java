package com.ong.api_backend.controller;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/gerencia/eventos")
public class EventoController {
    private static final Logger logger = LoggerFactory.getLogger(EventoController.class);
    private final EventoService service;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public EventoController(EventoService service) {
        this.service = service;
        logger.debug("EventoController initialized");
    }

    @PostMapping
    public ResponseEntity<?> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem) {
        logger.info("Received request to add new Evento with texto: {}", texto);
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O campo texto é obrigatório.");
            }
            Evento evento = new Evento();
            evento.setTexto(texto);
            Evento saved = service.salvar(evento, imagem);
            logger.info("Evento saved successfully with ID: {}", saved.getId());
            return ResponseEntity.status(201).body(saved);
        } catch (IOException e) {
            logger.error("Error saving Evento: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro ao salvar evento: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error saving Evento: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro inesperado ao salvar evento.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem) {
        logger.info("Received request to update Evento with ID: {}", id);
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O campo texto é obrigatório.");
            }
            Evento updated = service.atualizar(id, texto, imagem);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            logger.info("Evento updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            logger.error("Error updating Evento with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro ao atualizar evento: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating Evento with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro inesperado ao atualizar evento.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        logger.info("Received request to delete Evento with ID: {}", id);
        try {
            service.deletar(id);
            logger.info("Evento deleted successfully with ID: {}", id);
            return ResponseEntity.ok("Evento deletado com sucesso");
        } catch (IOException e) {
            logger.error("Error deleting Evento with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro ao deletar evento: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting Evento with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro inesperado ao deletar evento.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos() {
        logger.info("Received request to list all Eventos");
        List<Evento> eventos = service.listarTodos();
        logger.debug("Returning {} Eventos", eventos.size());
        return ResponseEntity.ok(eventos);
    }
}