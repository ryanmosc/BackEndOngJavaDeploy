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
    public ResponseEntity<?> listarTodosForms(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Iniciando carregamento dos formulários do dashboard",
                ip
        );

        salvarLog(
                ip,
                "Acesso ao dashboard de formulários"
        );

        try {

            Map<String, Object> resposta = new HashMap<>();

            resposta.put(
                    "voluntarios",
                    formularioCadastroVoluntarioRepository.findAll()
            );

            resposta.put(
                    "doancoesMensais",
                    formularioDoacaoMensalRepository.findAll()
            );

            resposta.put(
                    "faleConosco",
                    faleConoscoRepository.findAll()
            );

            logger.info(
                    "[IP: {}] Formulários carregados com sucesso",
                    ip
            );

            logger.debug(
                    "[IP: {}] Dashboard retornado com dados de voluntários, doações e fale conosco",
                    ip
            );

            salvarLog(
                    ip,
                    "Dashboard carregado com sucesso"
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao carregar formulários do dashboard: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao carregar dashboard: " + e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body("Erro ao carregar formulários");
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> enviarRespostaEmail(
            @RequestBody EmailDashboardRequest request,
            HttpServletRequest httpRequest) {

        String ip = IpUtil.getClientIp(httpRequest);

        logger.info(
                "[IP: {}] Iniciando envio de resposta por email",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de envio de email para " + request.email()
        );

        try {

            logger.info(
                    "[IP: {}] Enviando email para {} | Assunto: {}",
                    ip,
                    request.email(),
                    request.assunto()
            );

            emailService.enviarEmail(
                    request.email(),
                    request.assunto(),
                    request.mensagem()
            );

            logger.info(
                    "[IP: {}] Email enviado com sucesso para {}",
                    ip,
                    request.email()
            );

            salvarLog(
                    ip,
                    "Email enviado com sucesso para " + request.email()
            );

            respostaService.registrar(
                    request.nomeFormulario(),
                    request.nome(),
                    request.email(),
                    request.mensagemOriginal(),
                    request.mensagem()
            );

            logger.info(
                    "[IP: {}] Resposta registrada com sucesso | Formulário: {} | Destinatário: {}",
                    ip,
                    request.nomeFormulario(),
                    request.email()
            );

            salvarLog(
                    ip,
                    "Resposta registrada no sistema para " + request.email()
            );

            return ResponseEntity.ok(
                    "Email enviado e resposta salva com sucesso!"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao enviar resposta de email: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao enviar email para "
                            + request.email()
                            + ": "
                            + e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body("Erro ao enviar email");
        }
    }

    private void salvarLog(String ip, String mensagem) {

        Logs log = new Logs();

        log.setIp_requisicao(ip);
        log.setLog(mensagem);
        log.setLocalDateTime(LocalDateTime.now());

        logsService.salvarLog(log);
    }
}