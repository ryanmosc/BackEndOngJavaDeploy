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
import java.util.HashMap;
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
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "criar_atualizacao", "/api/gerencia/atualizacoes", request
        );

        try {
            if (texto == null || texto.trim().isEmpty()) {

                payload.put("Status", "Falha");
                payload.put("Erro", "Texto vazio");
                payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

                logger.warn("[IP: {}] Texto vazio ao criar atualização", ip);
                logsService.salvarLog(payload);

                return ResponseEntity.badRequest().build();
            }

            if (imagem != null) {
                payload.put("Imagem-Nome", imagem.getOriginalFilename());
                payload.put("Imagem-Tamanho", imagem.getSize());

                logger.info("[IP: {}] Upload recebido -> Nome: {} | Tamanho: {} bytes",
                        ip, imagem.getOriginalFilename(), imagem.getSize());
            }

            Atualizacoes atualizacao = new Atualizacoes();
            atualizacao.setTexto(texto);

            Atualizacoes saved = service.salvar(atualizacao, imagem);

            payload.put("Status", "Sucesso");
            payload.put("AtualizacaoId", saved.getId());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Atualização criada com sucesso. ID: {}", ip, saved.getId());
            logsService.salvarLog(payload);

            return ResponseEntity.status(201).body(saved);

        } catch (IOException e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro de IO: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro de IO ao salvar atualização: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            throw e;

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", "Erro inesperado: " + e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro inesperado ao salvar atualização: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

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
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "editar_atualizacao", "/api/gerencia/atualizacoes/" + id, request
        );
        payload.put("AtualizacaoId", id);

        if (imagem != null) {
            payload.put("Imagem-Nome", imagem.getOriginalFilename());
            payload.put("Imagem-Tamanho", imagem.getSize());
        }

        try {
            Atualizacoes updated = service.atualizar(id, texto, imagem);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Atualização editada com sucesso. ID: {}", ip, id);
            logsService.salvarLog(payload);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao atualizar ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(
            @PathVariable Long id,
            HttpServletRequest request) throws IOException {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "deletar_atualizacao", "/api/gerencia/atualizacoes/" + id, request
        );
        payload.put("AtualizacaoId", id);

        try {
            service.deletar(id);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Atualização removida com sucesso. ID: {}", ip, id);
            logsService.salvarLog(payload);

            return ResponseEntity.ok("Atualização deletada com sucesso");

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao deletar ID {}: {}", ip, id, e.getMessage(), e);
            logsService.salvarLog(payload);

            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Atualizacoes>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_atualizacoes", "/api/gerencia/atualizacoes", request
        );

        List<Atualizacoes> lista = service.listarTodos();

        payload.put("Status", "Sucesso");
        payload.put("TotalRegistros", lista.size());
        payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

        logger.info("[IP: {}] {} atualizações encontradas", ip, lista.size());
        logsService.salvarLog(payload);

        return ResponseEntity.ok(lista);
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