package com.ong.api_backend.controller;

import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.repository.LogsRepository;
import com.ong.api_backend.service.AtualizacoesService;
import com.ong.api_backend.service.LogsService;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gerencia/atualizacoes")
public class AtualizacoesController {

    private static final Logger logger =
            LoggerFactory.getLogger(AtualizacoesController.class);

    private final AtualizacoesService service;
    private final IpUtil ipUtil;
    private final LogsService logsService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AtualizacoesController(
            AtualizacoesService service,
            IpUtil ipUtil, LogsService logsService) {

        this.service = service;
        this.ipUtil = ipUtil;
        this.logsService = logsService;

        logger.debug("AtualizacoesController initialized");
    }

    @PostMapping
    public ResponseEntity<Atualizacoes> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) throws IOException {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to add new Atualizacoes with texto: {}",
                ip,
                texto
        );
        logsService.salvarLog(new Logs(ip, ));

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn(
                        "[IP: {}] Texto field is empty while creating Atualizacoes",
                        ip
                );

                return ResponseEntity.badRequest().build();
            }

            if (imagem != null) {

                logger.info(
                        "[IP: {}] Upload image received. Name: {} | Size: {} bytes",
                        ip,
                        imagem.getOriginalFilename(),
                        imagem.getSize()
                );
            }

            Atualizacoes atualizacoes = new Atualizacoes();
            atualizacoes.setTexto(texto);

            Atualizacoes saved = service.salvar(atualizacoes, imagem);

            logger.info(
                    "[IP: {}] Atualizacoes saved successfully with ID: {}",
                    ip,
                    saved.getId()
            );

            return ResponseEntity
                    .status(201)
                    .body(saved);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Error saving Atualizacoes: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Unexpected error saving Atualizacoes: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atualizacoes> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) throws IOException {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to update Atualizacoes with ID: {}",
                ip,
                id
        );

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn(
                        "[IP: {}] Texto field is empty while updating Atualizacoes ID: {}",
                        ip,
                        id
                );

                return ResponseEntity.badRequest().build();
            }

            if (imagem != null) {

                logger.info(
                        "[IP: {}] New image received for Atualizacoes ID {}. Name: {} | Size: {} bytes",
                        ip,
                        id,
                        imagem.getOriginalFilename(),
                        imagem.getSize()
                );
            }

            Atualizacoes updated = service.atualizar(
                    (long) Math.toIntExact(id),
                    texto,
                    imagem
            );

            logger.info(
                    "[IP: {}] Atualizacoes updated successfully with ID: {}",
                    ip,
                    id
            );

            return ResponseEntity.ok(updated);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Error updating Atualizacoes with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Unexpected error updating Atualizacoes with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(
            @PathVariable Long id,
            HttpServletRequest request) throws IOException {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to delete Atualizacoes with ID: {}",
                ip,
                id
        );

        try {

            service.deletar((long) Math.toIntExact(id));

            logger.info(
                    "[IP: {}] Atualizacoes deleted successfully with ID: {}",
                    ip,
                    id
            );

            return ResponseEntity.ok("Atualização deletada com sucesso");

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error deleting Atualizacoes with ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Atualizacoes>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to list all Atualizacoes",
                ip
        );

        List<Atualizacoes> eventos = service.listarTodos();

        logger.debug(
                "[IP: {}] Returning {} Atualizacoes",
                ip,
                eventos.size()
        );

        return ResponseEntity.ok(eventos);
    }
}