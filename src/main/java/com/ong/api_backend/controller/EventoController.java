package com.ong.api_backend.controller;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.EventoService;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/gerencia/eventos")
@RequiredArgsConstructor
public class EventoController {

    private static final Logger logger =
            LoggerFactory.getLogger(EventoController.class);

    private final EventoService service;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<?> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "criar_evento", "/api/gerencia/eventos", request
        );

        try {
            if (texto == null || texto.trim().isEmpty()) {

                payload.put("Status", "Falha");
                payload.put("Erro", "Texto vazio");
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.warn("[IP: {}] Campo texto vazio ao criar evento", ip);
                logsService.salvarLog(payload);

                return ResponseEntity.badRequest().body("O campo texto é obrigatório.");
            }

            if (imagem != null) {
                payload.put("Imagem-Nome", imagem.getOriginalFilename());
                payload.put("Imagem-Tamanho", imagem.getSize());

                logger.info("[IP: {}] Upload recebido -> Nome: {} | Tamanho: {} bytes",
                        ip, imagem.getOriginalFilename(), imagem.getSize());
            }

            Evento evento = new Evento();
            evento.setTexto(texto);

            Evento saved = service.salvar(evento, imagem);

            payload.put("Status", "Sucesso");
            payload.put("EventoId", saved.getId());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Evento criado com sucesso. ID: {}", ip, saved.getId());
            logsService.salvarLog(payload);

            return ResponseEntity.status(201).body(saved);

        } catch (IOException e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro de IO: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro IO ao salvar evento: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro ao salvar evento: " + e.getMessage());

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro inesperado: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro inesperado ao salvar evento: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro inesperado ao salvar evento.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "editar_evento", "/api/gerencia/eventos/" + id, request
        );
        payload.put("EventoId", id);

        if (imagem != null) {
            payload.put("Imagem-Nome", imagem.getOriginalFilename());
            payload.put("Imagem-Tamanho", imagem.getSize());
        }

        try {
            if (texto == null || texto.trim().isEmpty()) {

                payload.put("Status", "Falha");
                payload.put("Erro", "Texto vazio");
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.warn("[IP: {}] Campo texto vazio ao atualizar evento ID {}", ip, id);
                logsService.salvarLog(payload);

                return ResponseEntity.badRequest().body("O campo texto é obrigatório.");
            }

            Evento updated = service.atualizar(id, texto, imagem);

            if (updated == null) {

                payload.put("Status", "Falha");
                payload.put("Erro", "Evento não encontrado");
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.warn("[IP: {}] Evento não encontrado para atualização. ID: {}", ip, id);
                logsService.salvarLog(payload);

                return ResponseEntity.notFound().build();
            }

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Evento atualizado com sucesso. ID: {}", ip, id);
            logsService.salvarLog(payload);

            return ResponseEntity.ok(updated);

        } catch (IOException e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro de IO: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro IO ao atualizar evento ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro ao atualizar evento: " + e.getMessage());

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro inesperado: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro inesperado ao atualizar evento ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro inesperado ao atualizar evento.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "deletar_evento", "/api/gerencia/eventos/" + id, request
        );
        payload.put("EventoId", id);

        try {
            service.deletar(id);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Evento removido com sucesso. ID: {}", ip, id);
            logsService.salvarLog(payload);

            return ResponseEntity.ok("Evento deletado com sucesso");

        } catch (IOException e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro de IO: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro IO ao deletar evento ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro ao deletar evento: " + e.getMessage());

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro inesperado: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro inesperado ao deletar evento ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.status(500).body("Erro inesperado ao deletar evento.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos(HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_eventos", "/api/gerencia/eventos", request
        );

        List<Evento> eventos = service.listarTodos();

        payload.put("Status", "Sucesso");
        payload.put("TotalRegistros", eventos.size());
        payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

        logger.info("[IP: {}] {} eventos encontrados", ip, eventos.size());
        logsService.salvarLog(payload);

        return ResponseEntity.ok(eventos);
    }

    private HashMap<String, Object> construirPayloadBase(
            String ip,
            String acao,
            String endpoint,
            HttpServletRequest request) {

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("Ip", ip);
        payload.put("Action", acao);
        payload.put("End-Point", endpoint);
        payload.put("Method", request.getMethod());
        payload.put("User-Agent", request.getHeader("User-Agent"));
        payload.put("Hour", LocalDateTime.now().toString());
        return payload;
    }
}