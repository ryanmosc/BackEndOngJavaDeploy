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
import java.util.List;

@RestController
@RequestMapping("/api/seja_voluntario")
@RequiredArgsConstructor
public class FormularioCadastroVoluntarioController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioCadastroVoluntarioController.class);

    private final FormularioCadastroVoluntarioService
            formularioCadastroVoluntarioService;

    private final EmailService emailService;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioCadastroVoluntario formularioCadastroVoluntario,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Iniciando recebimento de cadastro de voluntário",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de cadastro de voluntário"
        );

        try {

            logger.info(
                    "[IP: {}] Dados recebidos | Nome: {} | Email: {}",
                    ip,
                    formularioCadastroVoluntario.getNome_completo(),
                    formularioCadastroVoluntario.getE_mail()
            );

            salvarLog(
                    ip,
                    "Cadastro recebido de "
                            + formularioCadastroVoluntario.getNome_completo()
                            + " | Email: "
                            + formularioCadastroVoluntario.getE_mail()
            );

            formularioCadastroVoluntarioService
                    .saveAllFormularioCadastroVoluntariosService(
                            formularioCadastroVoluntario
                    );

            logger.info(
                    "[IP: {}] Cadastro de voluntário salvo com sucesso",
                    ip
            );

            salvarLog(
                    ip,
                    "Cadastro de voluntário salvo com sucesso"
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao salvar cadastro de voluntário: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao salvar cadastro de voluntário: "
                            + e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioCadastroVoluntario>> getAll(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Listando todos os cadastros de voluntários",
                ip
        );

        salvarLog(
                ip,
                "Listagem de cadastros de voluntários"
        );

        List<FormularioCadastroVoluntario> lista =
                formularioCadastroVoluntarioService.listarTodos();

        logger.info(
                "[IP: {}] {} cadastros encontrados",
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