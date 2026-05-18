package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FormularioDoacaoMensalService;
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
@RequestMapping("/api/doacao_mensal")
@RequiredArgsConstructor
public class FormularioDoacaoMensalController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioDoacaoMensalController.class);

    private final FormularioDoacaoMensalService formularioDoacaoMensalService;
    private final EmailService emailService;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioDoacaoMensal formulario,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "cadastrar_doacao_mensal", "/api/doacao_mensal", request
        );
        payload.put("Nome", formulario.getNomeCompleto());
        payload.put("Email", formulario.getEmail());

        try {
            formularioDoacaoMensalService
                    .saveAllFormularioDoacaoMensalService(formulario);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Cadastro de doação mensal salvo com sucesso | Nome: {} | Email: {}",
                    ip, formulario.getNomeCompleto(), formulario.getEmail());
            logsService.salvarLog(payload);

            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao salvar cadastro de doação mensal: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.badRequest()
                    .body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioDoacaoMensal>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_doacoes_mensais", "/api/doacao_mensal", request
        );

        List<FormularioDoacaoMensal> lista =
                formularioDoacaoMensalService.listarTodosMensal();

        payload.put("Status", "Sucesso");
        payload.put("TotalRegistros", lista.size());
        payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

        logger.info("[IP: {}] {} cadastros encontrados", ip, lista.size());
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