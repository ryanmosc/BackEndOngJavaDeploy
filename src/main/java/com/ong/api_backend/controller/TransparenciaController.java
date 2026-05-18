package com.ong.api_backend.controller;

import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.util.IpUtil;
import com.ong.api_backend.util.TransparenciaFileStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/api/transparencia")
@RequiredArgsConstructor
public class TransparenciaController {

    private static final Logger logger =
            LoggerFactory.getLogger(TransparenciaController.class);

    private final TransparenciaFileStorage fileStorage;
    private final LogsService logsService;

    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarBalancete(HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "visualizar_balancete", "/api/transparencia/visualizar", request
        );

        try {
            ResponseEntity<byte[]> response = fileStorage.visualizarBalancete();

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Balancete visualizado com sucesso", ip);
            logsService.salvarLog(payload);

            return response;

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao visualizar balancete: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping(value = "/enviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarBalancete(
            @RequestParam("balancete") MultipartFile balancete,
            @RequestParam("texto") String texto,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "enviar_balancete", "/api/transparencia/enviar", request
        );
        payload.put("Arquivo-Nome", balancete.getOriginalFilename());
        payload.put("Arquivo-Tamanho", balancete.getSize());

        try {
            fileStorage.salvarBalancete(balancete, texto);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Balancete enviado com sucesso | Arquivo: {} | Tamanho: {} bytes",
                    ip, balancete.getOriginalFilename(), balancete.getSize());
            logsService.salvarLog(payload);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao enviar balancete: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.internalServerError().build();
        }
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