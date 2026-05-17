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

        logger.info(
                "[IP: {}] Iniciando criação de evento",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de criação de evento"
        );

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn(
                        "[IP: {}] Campo texto vazio ao criar evento",
                        ip
                );

                salvarLog(
                        ip,
                        "Falha ao criar evento: texto vazio"
                );

                return ResponseEntity
                        .badRequest()
                        .body("O campo texto é obrigatório.");
            }

            if (imagem != null) {

                logger.info(
                        "[IP: {}] Upload recebido -> Nome: {} | Tamanho: {} bytes",
                        ip,
                        imagem.getOriginalFilename(),
                        imagem.getSize()
                );

                salvarLog(
                        ip,
                        "Imagem enviada: " + imagem.getOriginalFilename()
                );
            }

            Evento evento = new Evento();
            evento.setTexto(texto);

            Evento saved = service.salvar(evento, imagem);

            logger.info(
                    "[IP: {}] Evento criado com sucesso. ID: {}",
                    ip,
                    saved.getId()
            );

            salvarLog(
                    ip,
                    "Evento criado com sucesso. ID: " + saved.getId()
            );

            return ResponseEntity
                    .status(201)
                    .body(saved);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Erro IO ao salvar evento: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro IO ao criar evento: " + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao salvar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro inesperado ao salvar evento: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro inesperado ao criar evento: " + e.getMessage()
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

        logger.info(
                "[IP: {}] Iniciando atualização do evento ID {}",
                ip,
                id
        );

        salvarLog(
                ip,
                "Tentativa de atualização do evento ID " + id
        );

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn(
                        "[IP: {}] Campo texto vazio ao atualizar evento ID {}",
                        ip,
                        id
                );

                salvarLog(
                        ip,
                        "Falha ao atualizar evento ID "
                                + id
                                + ": texto vazio"
                );

                return ResponseEntity
                        .badRequest()
                        .body("O campo texto é obrigatório.");
            }

            Evento updated = service.atualizar(id, texto, imagem);

            if (updated == null) {

                logger.warn(
                        "[IP: {}] Evento não encontrado para atualização. ID: {}",
                        ip,
                        id
                );

                salvarLog(
                        ip,
                        "Evento não encontrado para atualização. ID: " + id
                );

                return ResponseEntity.notFound().build();
            }

            logger.info(
                    "[IP: {}] Evento atualizado com sucesso. ID: {}",
                    ip,
                    id
            );

            salvarLog(
                    ip,
                    "Evento atualizado com sucesso. ID: " + id
            );

            return ResponseEntity.ok(updated);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Erro IO ao atualizar evento ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro IO ao atualizar evento ID "
                            + id
                            + ": "
                            + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao atualizar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro inesperado ao atualizar evento ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro inesperado ao atualizar evento ID "
                            + id
                            + ": "
                            + e.getMessage()
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

        logger.info(
                "[IP: {}] Iniciando exclusão do evento ID {}",
                ip,
                id
        );

        salvarLog(
                ip,
                "Tentativa de exclusão do evento ID " + id
        );

        try {

            service.deletar(id);

            logger.info(
                    "[IP: {}] Evento removido com sucesso. ID: {}",
                    ip,
                    id
            );

            salvarLog(
                    ip,
                    "Evento removido com sucesso. ID: " + id
            );

            return ResponseEntity.ok("Evento deletado com sucesso");

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Erro IO ao deletar evento ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro IO ao deletar evento ID "
                            + id
                            + ": "
                            + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body("Erro ao deletar evento: " + e.getMessage());

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro inesperado ao deletar evento ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro inesperado ao deletar evento ID "
                            + id
                            + ": "
                            + e.getMessage()
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

        logger.info(
                "[IP: {}] Listando todos os eventos",
                ip
        );

        salvarLog(
                ip,
                "Listagem de eventos"
        );

        List<Evento> eventos = service.listarTodos();

        logger.info(
                "[IP: {}] {} eventos encontrados",
                ip,
                eventos.size()
        );

        return ResponseEntity.ok(eventos);
    }

    private void salvarLog(String ip, String mensagem) {

        Logs log = new Logs();

        log.setIp_requisicao(ip);
        log.setLog(mensagem);
        log.setLocalDateTime(LocalDateTime.now());

        logsService.salvarLog(log);
    }
}