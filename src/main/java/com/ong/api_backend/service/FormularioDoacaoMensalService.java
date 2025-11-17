package com.ong.api_backend.service;


import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.repository.FormularioDoacaoMensalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormularioDoacaoMensalService {
    private static final Logger logger = LoggerFactory.getLogger(FormularioDoacaoMensalService.class);

    @Autowired
    private FormularioDoacaoMensalRepository formularioDoacaoMensalRepository;

    @Autowired
    private EmailService emailService;


    public void saveAllFormularioDoacaoMensalService(FormularioDoacaoMensal formularioDoacaoMensal) {
        logger.info("Attempting to save FormularioDoacaoMensal");
        try {
            formularioDoacaoMensalRepository.saveAndFlush(formularioDoacaoMensal);
            logger.info("FormularioDoacaoMensal saved successfully");
            emailService.enviarEmail(
                    formularioDoacaoMensal.getEmail(),
                    "Recebemos sua mensagem!",
                    "Olá " + formularioDoacaoMensal.getNomeCompleto() + ", Recebemos sua mensagem! Em breve responderemos."
            );
            logger.info("FaleConosco e-mail send successfully");
        } catch (Exception e) {
            logger.error("Error saving FormularioDoacaoMensal: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FormularioDoacaoMensal> listarTodosMensal() {
        return formularioDoacaoMensalRepository.findAll();
    }
}