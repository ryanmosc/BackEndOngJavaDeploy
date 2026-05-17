package com.ong.api_backend.controller;

import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.AtualizacoesService;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gerencia/atualizacoes")
@RequiredArgsConstructor
public class AtualizacoesController {

    private static final Logger logger =
            LoggerFactory.getLogger(AtualizacoesController.class);

    private final AtualizacoesService service;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<Atualizacoes> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "file-1", required = false) MultipartFile imagem,
            HttpServletRequest request) throws IOException {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Iniciando criação de atualização", ip);

        salvarLog(ip, "Tentativa de criação de atualização");

        try {

            if (texto == null || texto.trim().isEmpty()) {

                logger.warn("[IP: {}] Texto vazio ao criar atualização", ip);

                salvarLog(ip, "Falha ao criar atualização: texto vazio");

                return ResponseEntity.badRequest().build();
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

            Atualizacoes atualizacao = new Atualizacoes();
            atualizacao.setTexto(texto);

            Atualizacoes saved = service.salvar(atualizacao, imagem);

            logger.info(
                    "[IP: {}] Atualização criada com sucesso. ID: {}",
                    ip,
                    saved.getId()
            );

            salvarLog(
                    ip,
                    "Atualização criada com sucesso. ID: " + saved.getId()
            );

            return ResponseEntity.status(201).body(saved);

        } catch (IOException e) {

            logger.error(
                    "[IP: {}] Erro de IO ao salvar atualização: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro IO ao criar atualização: " + e.getMessage()
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro inesperado ao salvar atualização: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro inesperado ao criar atualização: " + e.getMessage()
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
                "[IP: {}] Iniciando atualização da atualização ID {}",
                ip,
                id
        );

        salvarLog(
                ip,
                "Tentativa de atualização da atualização ID " + id
        );

        try {

            Atualizacoes updated = service.atualizar(
                    id,
                    texto,
                    imagem
            );

            logger.info(
                    "[IP: {}] Atualização editada com sucesso. ID: {}",
                    ip,
                    id
            );

            salvarLog(
                    ip,
                    "Atualização editada com sucesso. ID: " + id
            );

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao atualizar ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao atualizar ID " + id + ": " + e.getMessage()
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
                "[IP: {}] Solicitação de exclusão da atualização ID {}",
                ip,
                id
        );

        salvarLog(
                ip,
                "Tentativa de exclusão da atualização ID " + id
        );

        try {

            service.deletar(id);

            logger.info(
                    "[IP: {}] Atualização removida com sucesso. ID: {}",
                    ip,
                    id
            );

            salvarLog(
                    ip,
                    "Atualização removida com sucesso. ID: " + id
            );

            return ResponseEntity.ok("Atualização deletada com sucesso");

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao deletar ID {}: {}",
                    ip,
                    id,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao deletar ID " + id + ": " + e.getMessage()
            );

            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Atualizacoes>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info("[IP: {}] Listando todas as atualizações", ip);

        salvarLog(ip, "Listagem de atualizações");

        List<Atualizacoes> lista = service.listarTodos();

        logger.info(
                "[IP: {}] {} atualizações encontradas",
                ip,
                lista.size()
        );

        return ResponseEntity.ok(lista);
    }

    private void salvarLog(String ip, String mensagem) {

        Logs log = new Logs();

        log.setIp_requisicao(ip);
        log.setLog(mensagem);
        log.setLocalDateTime(LocalDateTime.now());

        logsService.salvarLog(log);
    }
}