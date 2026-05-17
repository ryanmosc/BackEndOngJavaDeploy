package com.ong.api_backend.controller;

import com.ong.api_backend.model.EmailDashboardRequest;
import com.ong.api_backend.repository.FaleConoscoRepository;
import com.ong.api_backend.repository.FormularioCadastroVoluntarioRepository;
import com.ong.api_backend.repository.FormularioDoacaoMensalRepository;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.RespostaService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger =
            LoggerFactory.getLogger(DashboardController.class);

    private final EmailService emailService;
    private final RespostaService respostaService;
    private final FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository;
    private final FormularioDoacaoMensalRepository formularioDoacaoMensalRepository;
    private final FaleConoscoRepository faleConoscoRepository;
    private final IpUtil ipUtil;

    @Autowired
    public DashboardController(
            EmailService emailService,
            RespostaService respostaService,
            FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository,
            FormularioDoacaoMensalRepository formularioDoacaoMensalRepository,
            FaleConoscoRepository faleConoscoRepository,
            IpUtil ipUtil) {

        this.emailService = emailService;
        this.faleConoscoRepository = faleConoscoRepository;
        this.formularioDoacaoMensalRepository = formularioDoacaoMensalRepository;
        this.formularioCadastroVoluntarioRepository = formularioCadastroVoluntarioRepository;
        this.respostaService = respostaService;
        this.ipUtil = ipUtil;

        logger.debug("DashboardController initialized");
    }

    @GetMapping("/formularios")
    public ResponseEntity<?> listarTodosForms(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to list all dashboard forms",
                ip
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
                    "[IP: {}] Dashboard forms loaded successfully",
                    ip
            );

            logger.debug(
                    "[IP: {}] Dashboard response contains volunteer, donation and contact form data",
                    ip
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error loading dashboard forms: {}",
                    ip,
                    e.getMessage(),
                    e
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
                "[IP: {}] Received request to send dashboard email response",
                ip
        );

        try {

            logger.info(
                    "[IP: {}] Sending email response to {} | Subject: {}",
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
                    "[IP: {}] Email sent successfully to {}",
                    ip,
                    request.email()
            );

            respostaService.registrar(
                    request.nomeFormulario(),
                    request.nome(),
                    request.email(),
                    request.mensagemOriginal(),
                    request.mensagem()
            );

            logger.info(
                    "[IP: {}] Email response registered successfully | Form: {} | Recipient: {}",
                    ip,
                    request.nomeFormulario(),
                    request.email()
            );

            return ResponseEntity.ok(
                    "Email enviado e resposta salva com sucesso!"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error sending dashboard email response: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            return ResponseEntity
                    .internalServerError()
                    .body("Erro ao enviar email");
        }
    }
}