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

        logger.info(
                "[IP: {}] Iniciando recebimento de mensagem do formulário de contato",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de envio de mensagem no formulário Fale Conosco"
        );

        try {

            logger.info(
                    "[IP: {}] Formulário recebido | Nome: {} | Email: {}",
                    ip,
                    faleConosco.getNomeCompleto(),
                    faleConosco.getEmail()
            );

            salvarLog(
                    ip,
                    "Formulário recebido de "
                            + faleConosco.getNomeCompleto()
                            + " | Email: "
                            + faleConosco.getEmail()
            );

            faleConoscoService.saveAllService(faleConosco);

            logger.info(
                    "[IP: {}] Mensagem salva com sucesso",
                    ip
            );

            salvarLog(
                    ip,
                    "Mensagem Fale Conosco salva com sucesso"
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao salvar mensagem Fale Conosco: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao salvar mensagem Fale Conosco: "
                            + e.getMessage()
            );

            throw new DadosInvalidosException(
                    "Dados inválidos ou faltantes"
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<FaleConosco>> listarTodos(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Listando todas as mensagens de contato",
                ip
        );

        salvarLog(
                ip,
                "Listagem de mensagens Fale Conosco"
        );

        List<FaleConosco> lista =
                faleConoscoService.listarTodosFale();

        logger.info(
                "[IP: {}] {} mensagens encontradas",
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