package com.ong.api_backend.controller;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.service.EventoService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final IpUtil ipUtil;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public EventoController(EventoService service, IpUtil ipUtil) {
        this.service = service;
        this.ipUtil = ipUtil;

        logger.debug("EventoController initialized");
    }

    @PostMapping
    public ResponseEntity<?> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Received request to add new Evento with texto: {}", ip, texto);

        try {

            if (texto == null || texto.trim().isEmpty()) {
                logger.warn("[IP: {}] Texto field is empty while creating Evento", ip);

                return ResponseEntity
                        .badRequest()
                        .body("O campo texto é obrigatório.");
            }

            Evento evento = new Evento();
            evento.setTexto(texto);

            Evento saved = service.salvar(evento, imagem);

            logger.info("[IP: {}] Evento saved successfully with ID: {}", ip, saved.getId());

            return ResponseEntity
                    .status(201)
                    .body(saved);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Error saving Evento: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao salvar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Unexpected error saving Evento: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro inesperado ao salvar evento.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Received request to update Evento with ID: {}", ip, id);

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn(
                        "[IP: {}] Texto field is empty while updating Evento ID: {}",
                        ip,
                        id
                );

                return ResponseEntity
                        .badRequest()
                        .body("O campo texto é obrigatório.");
            }

            Evento updated = service.atualizar(id, texto, imagem);

            if (updated == null) {

                logger.warn(
                        "[IP: {}] Evento not found for update. ID: {}",
                        ip,
                        id
                );

                return ResponseEntity.notFound().build();
            }

            logger.info(
                    "[IP: {}] Evento updated successfully with ID: {}",
                    ip,
                    id
            );

            return ResponseEntity.ok(updated);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Error updating Evento with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao atualizar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Unexpected error updating Evento with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro inesperado ao atualizar evento.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Received request to delete Evento with ID: {}", ip, id);

        try {

            service.deletar(id);

            logger.info(
                    "[IP: {}] Evento deleted successfully with ID: {}",
                    ip,
                    id
            );

            return ResponseEntity.ok("Evento deletado com sucesso");

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Error deleting Evento with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao deletar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Unexpected error deleting Evento with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro inesperado ao deletar evento.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Received request to list all Eventos", ip);

        List<Evento> eventos = service.listarTodos();

        logger.debug(
                "[IP: {}] Returning {} Eventos",
                ip,
                eventos.size()
        );

        return ResponseEntity.ok(eventos);
    }
}