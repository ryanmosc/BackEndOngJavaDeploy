package com.ong.api_backend.service;


import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.repository.FormularioCadastroVoluntarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormularioCadastroVoluntarioService {
    private static final Logger logger = LoggerFactory.getLogger(FormularioCadastroVoluntarioService.class);

    @Autowired
    private FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository;



    public void saveAllFormularioCadastroVoluntariosService(FormularioCadastroVoluntario formularioCadastroVoluntario) {
        logger.info("Attempting to save FormularioCadastroVoluntario");
        try {
            formularioCadastroVoluntarioRepository.saveAndFlush(formularioCadastroVoluntario);
            logger.info("FormularioCadastroVoluntario saved successfully");
        } catch (Exception e) {
            logger.error("Error saving FormularioCadastroVoluntario: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FormularioCadastroVoluntario> listarTodos(){
        return  formularioCadastroVoluntarioRepository.findAll();
    }
}