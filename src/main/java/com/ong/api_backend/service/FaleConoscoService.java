package com.ong.api_backend.service;


import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.repository.FaleConoscoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaleConoscoService {
    private static final Logger logger = LoggerFactory.getLogger(FaleConoscoService.class);

    @Autowired
    private FaleConoscoRepository faleConoscoRepository;

    @Autowired
    private EmailService emailService;



    public void saveAllService(FaleConosco faleConosco) {
        logger.info("Attempting to save FaleConosco message");
        try {
            faleConoscoRepository.saveAndFlush(faleConosco);
            logger.info("FaleConosco message saved successfully");

            emailService.enviarEmail(
                    faleConosco.getEmail(),
                    "Recebemos sua mensagem!",
                    "Olá " + faleConosco.getNomeCompleto() + ", Recebemos sua mensagem! Em breve responderemos."
            );
            logger.info("FaleConosco e-mail send successfully");


        } catch (Exception e) {
            logger.error("Error saving FaleConosco message: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FaleConosco> listarTodosFale() {
        return faleConoscoRepository.findAll();
    }
}