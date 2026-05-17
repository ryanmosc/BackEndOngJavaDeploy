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
import java.util.List;

@RestController
@RequestMapping("/api/doacao_mensal")
@RequiredArgsConstructor
public class FormularioDoacaoMensalController {

    private static final Logger logger =
            LoggerFactory.getLogger(FormularioDoacaoMensalController.class);

    private final FormularioDoacaoMensalService
            formularioDoacaoMensalService;

    private final EmailService emailService;
    private final LogsService logsService;

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FormularioDoacaoMensal formularioDoacaoMensal,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Iniciando recebimento de formulário de doação mensal",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de cadastro de doação mensal"
        );

        try {

            logger.info(
                    "[IP: {}] Dados recebidos | Nome: {} | Email: {}",
                    ip,
                    formularioDoacaoMensal.getNomeCompleto(),
                    formularioDoacaoMensal.getEmail()
            );

            salvarLog(
                    ip,
                    "Cadastro de doação mensal recebido de "
                            + formularioDoacaoMensal.getNomeCompleto()
                            + " | Email: "
                            + formularioDoacaoMensal.getEmail()
            );

            formularioDoacaoMensalService
                    .saveAllFormularioDoacaoMensalService(
                            formularioDoacaoMensal
                    );

            logger.info(
                    "[IP: {}] Cadastro de doação mensal salvo com sucesso",
                    ip
            );

            salvarLog(
                    ip,
                    "Cadastro de doação mensal salvo com sucesso"
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao salvar cadastro de doação mensal: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao salvar cadastro de doação mensal: "
                            + e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\": \"Erro ao enviar mensagem\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<FormularioDoacaoMensal>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Listando todos os cadastros de doação mensal",
                ip
        );

        salvarLog(
                ip,
                "Listagem de cadastros de doação mensal"
        );

        List<FormularioDoacaoMensal> lista =
                formularioDoacaoMensalService.listarTodosMensal();

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