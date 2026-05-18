package com.ong.api_backend.controller;

import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FormularioCadastroVoluntarioService;
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
@RequestMapping("/api/seja_voluntario")
@RequiredArgsConstructor
public class FormularioCadastroVoluntarioController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioCadastroVoluntarioController.class);

    private final FormularioCadastroVoluntarioService formularioCadastroVoluntarioService;
    private final EmailService emailService;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioCadastroVoluntario formulario,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "cadastrar_voluntario", "/api/seja_voluntario", request
        );
        payload.put("Nome", formulario.getNome_completo());
        payload.put("Email", formulario.getE_mail());

        try {
            formularioCadastroVoluntarioService
                    .saveAllFormularioCadastroVoluntariosService(formulario);

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Cadastro de voluntário salvo com sucesso | Nome: {} | Email: {}",
                    ip, formulario.getNome_completo(), formulario.getE_mail());
            logsService.salvarLog(payload);

            return ResponseEntity.ok("{\"message\": \"Mensagem enviada com sucesso\"}");

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao salvar cadastro de voluntário: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.badRequest()
                    .body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioCadastroVoluntario>> getAll(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_voluntarios", "/api/seja_voluntario", request
        );

        List<FormularioCadastroVoluntario> lista =
                formularioCadastroVoluntarioService.listarTodos();

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