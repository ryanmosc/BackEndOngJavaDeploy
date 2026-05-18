package com.ong.api_backend.controller;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FaleConoscoService;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/fale_conosco")
@RequiredArgsConstructor
public class FaleConoscoController {

    private static final Logger logger =
            LoggerFactory.getLogger(FaleConoscoController.class);

    private final EmailService emailService;
    private final FaleConoscoService faleConoscoService;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FaleConosco faleConosco,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "enviar_fale_conosco", "/api/fale_conosco", request
        );
        payload.put("Nome", faleConosco.getNomeCompleto());
        payload.put("Email", faleConosco.getEmail());

        try {
            faleConoscoService.saveAllService(faleConosco);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Mensagem Fale Conosco salva com sucesso | Nome: {} | Email: {}",
                    ip, faleConosco.getNomeCompleto(), faleConosco.getEmail());
            logsService.salvarLog(payload);

            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao salvar mensagem Fale Conosco: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            throw new DadosInvalidosException("Dados inválidos ou faltantes");
        }
    }

    @GetMapping
    public ResponseEntity<List<FaleConosco>> listarTodos(HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_fale_conosco", "/api/fale_conosco", request
        );

        List<FaleConosco> lista = faleConoscoService.listarTodosFale();

        payload.put("Status", "Sucesso");
        payload.put("TotalRegistros", lista.size());
        payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

        logger.info("[IP: {}] {} mensagens encontradas", ip, lista.size());
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
