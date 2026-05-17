package com.ong.api_backend.controller;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.FaleConoscoService;
import com.ong.api_backend.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fale_conosco")
public class FaleConoscoController {

    private static final Logger logger =
            LoggerFactory.getLogger(FaleConoscoController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private FaleConoscoService faleConoscoService;

    @Autowired
    private IpUtil ipUtil;

    public FaleConoscoController(
            FaleConoscoService faleConoscoService,
            EmailService emailService,
            IpUtil ipUtil) {

        this.faleConoscoService = faleConoscoService;
        this.emailService = emailService;
        this.ipUtil = ipUtil;

        logger.debug("FaleConoscoController initialized");
    }

    @PostMapping
    public ResponseEntity<String> saveAll(
            @RequestBody @Valid FaleConosco faleConosco,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Received request to save FaleConosco message",
                ip
        );

        try {

            logger.info(
                    "[IP: {}] Contact form received | Nome: {} | Email: {}",
                    ip,
                    faleConosco.getNomeCompleto(),
                    faleConosco.getEmail()
            );

            faleConoscoService.saveAllService(faleConosco);

            logger.info(
                    "[IP: {}] FaleConosco message saved successfully",
                    ip
            );

            return ResponseEntity.ok(
                    "{\"message\": \"Mensagem enviada com sucesso\"}"
            );

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Error saving FaleConosco message: {}",
                    ip,
                    e.getMessage(),
                    e
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
                "[IP: {}] Received request to list all contact messages",
                ip
        );

        List<FaleConosco> lista =
                faleConoscoService.listarTodosFale();

        logger.debug(
                "[IP: {}] Returning {} contact messages",
                ip,
                lista.size()
        );

        return ResponseEntity.ok(lista);
    }
}