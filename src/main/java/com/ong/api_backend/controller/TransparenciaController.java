package com.ong.api_backend.controller;

import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.LogsService;
import com.ong.api_backend.util.IpUtil;
import com.ong.api_backend.util.TransparenciaFileStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transparencia")
@RequiredArgsConstructor
public class TransparenciaController {

    private static final Logger logger =
            LoggerFactory.getLogger(TransparenciaController.class);

    private final TransparenciaFileStorage fileStorage;
    private final LogsService logsService;

    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarBalancete(
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Solicitação para visualizar balancete",
                ip
        );

        salvarLog(
                ip,
                "Visualização de balancete"
        );

        try {

            ResponseEntity<byte[]> response =
                    fileStorage.visualizarBalancete();

            logger.info(
                    "[IP: {}] Balancete visualizado com sucesso",
                    ip
            );

            salvarLog(
                    ip,
                    "Balancete visualizado com sucesso"
            );

            return response;

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao visualizar balancete: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao visualizar balancete: " + e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @PatchMapping(
            value = "/enviar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> enviarBalancete(
            @RequestParam("balancete") MultipartFile balancete,
            @RequestParam("texto") String texto,
            HttpServletRequest request) {

        String ip = IpUtil.getClientIp(request);

        logger.info(
                "[IP: {}] Iniciando envio de balancete",
                ip
        );

        salvarLog(
                ip,
                "Tentativa de envio de balancete"
        );

        try {

            logger.info(
                    "[IP: {}] Arquivo recebido -> Nome: {} | Tamanho: {} bytes",
                    ip,
                    balancete.getOriginalFilename(),
                    balancete.getSize()
            );

            salvarLog(
                    ip,
                    "Arquivo de balancete recebido: "
                            + balancete.getOriginalFilename()
            );

            fileStorage.salvarBalancete(
                    balancete,
                    texto
            );

            logger.info(
                    "[IP: {}] Balancete enviado com sucesso",
                    ip
            );

            salvarLog(
                    ip,
                    "Balancete enviado com sucesso"
            );

            return ResponseEntity.noContent().build();

        } catch (Exception e) {

            logger.error(
                    "[IP: {}] Erro ao enviar balancete: {}",
                    ip,
                    e.getMessage(),
                    e
            );

            salvarLog(
                    ip,
                    "Erro ao enviar balancete: " + e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .build();
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