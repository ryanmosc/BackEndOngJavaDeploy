package com.ong.api_backend.controller;

import com.ong.api_backend.model.EmailDashboardRequest;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.repository.FaleConoscoRepository;
import com.ong.api_backend.repository.FormularioCadastroVoluntarioRepository;
import com.ong.api_backend.repository.FormularioDoacaoMensalRepository;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.service.RespostaService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final Logger logger =
            LoggerFactory.getLogger(DashboardController.class);

    private final EmailService emailService;
    private final RespostaService respostaService;
    private final FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository;
    private final FormularioDoacaoMensalRepository formularioDoacaoMensalRepository;
    private final FaleConoscoRepository faleConoscoRepository;
    private final LogsService logsService;

    @GetMapping("/formularios")
    public ResponseEntity<?> listarTodosForms(HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "listar_formularios", "/api/dashboard/formularios", request
        );

        try {
            var voluntarios = formularioCadastroVoluntarioRepository.findAll();
            var doacoes = formularioDoacaoMensalRepository.findAll();
            var faleConosco = faleConoscoRepository.findAll();

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("voluntarios", voluntarios);
            resposta.put("doancoesMensais", doacoes);
            resposta.put("faleConosco", faleConosco);

            payload.put("Status", "Sucesso");
            payload.put("TotalVoluntarios", voluntarios.size());
            payload.put("TotalDoacoes", doacoes.size());
            payload.put("TotalFaleConosco", faleConosco.size());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Formulários carregados com sucesso", ip);
            logsService.salvarLog(payload);

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao carregar formulários do dashboard: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.internalServerError().body("Erro ao carregar formulários");
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> enviarRespostaEmail(
            @RequestBody EmailDashboardRequest emailRequest,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);
        long inicio = System.currentTimeMillis();

        HashMap<String, Object> payload = construirPayloadBase(
                ip, "enviar_email", "/api/dashboard/email", request
        );
        payload.put("Destinatario", emailRequest.email());
        payload.put("Assunto", emailRequest.assunto());
        payload.put("Formulario", emailRequest.nomeFormulario());
        payload.put("NomeDestinatario", emailRequest.nome());

        try {
            emailService.enviarEmail(
                    emailRequest.email(),
                    emailRequest.assunto(),
                    emailRequest.mensagem()
            );

            respostaService.registrar(
                    emailRequest.nomeFormulario(),
                    emailRequest.nome(),
                    emailRequest.email(),
                    emailRequest.mensagemOriginal(),
                    emailRequest.mensagem()
            );

            payload.put("Status", "Sucesso");
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.info("[IP: {}] Email enviado com sucesso para {}", ip, emailRequest.email());
            logsService.salvarLog(payload);

            return ResponseEntity.ok("Email enviado e resposta salva com sucesso!");

        } catch (Exception e) {

            payload.put("Status", "Falha");
            payload.put("Erro", e.getMessage());
            payload.put("DuracaoMs", System.currentTimeMillis() - inicio);

            logger.error("[IP: {}] Erro ao enviar resposta de email: {}", ip, e.getMessage(), e);
            logsService.salvarLog(payload);

            return ResponseEntity.internalServerError().body("Erro ao enviar email");
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