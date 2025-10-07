package com.ong.api_backend.service;

import com.ong.api_backend.dao.FormularioCadastroVoluntarioDao;
import com.ong.api_backend.model.FormularioCadastroVoluntario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormularioCadastroVoluntarioService {
    private static final Logger logger = LoggerFactory.getLogger(FormularioCadastroVoluntarioService.class);

    @Autowired
    private FormularioCadastroVoluntarioDao formularioCadastroVoluntarioDao;

    public FormularioCadastroVoluntarioService(FormularioCadastroVoluntarioDao formularioCadastroVoluntarioDao) {
        this.formularioCadastroVoluntarioDao = formularioCadastroVoluntarioDao;
        logger.debug("FormularioCadastroVoluntarioService initialized");
    }

    public void saveAllFormularioCadastroVoluntariosService(FormularioCadastroVoluntario formularioCadastroVoluntario) {
        logger.info("Attempting to save FormularioCadastroVoluntario");
        try {
            formularioCadastroVoluntarioDao.saveFormularioCadastroVoluntario(formularioCadastroVoluntario);
            logger.info("FormularioCadastroVoluntario saved successfully");
        } catch (Exception e) {
            logger.error("Error saving FormularioCadastroVoluntario: {}", e.getMessage(), e);
            throw e;
        }
    }
}