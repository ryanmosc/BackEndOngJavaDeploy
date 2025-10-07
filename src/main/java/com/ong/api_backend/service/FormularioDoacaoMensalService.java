package com.ong.api_backend.service;

import com.ong.api_backend.dao.FormularioDoacaoMensalDao;
import com.ong.api_backend.model.FormularioDoacaoMensal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormularioDoacaoMensalService {
    private static final Logger logger = LoggerFactory.getLogger(FormularioDoacaoMensalService.class);

    @Autowired
    private FormularioDoacaoMensalDao formularioDoacaoMensalDao;

    public FormularioDoacaoMensalService(FormularioDoacaoMensalDao formularioDoacaoMensalDao) {
        this.formularioDoacaoMensalDao = formularioDoacaoMensalDao;
        logger.debug("FormularioDoacaoMensalService initialized");
    }

    public void saveAllFormularioDoacaoMensalService(FormularioDoacaoMensal formularioDoacaoMensal) {
        logger.info("Attempting to save FormularioDoacaoMensal");
        try {
            formularioDoacaoMensalDao.saveFormularioDoacaoMensal(formularioDoacaoMensal);
            logger.info("FormularioDoacaoMensal saved successfully");
        } catch (Exception e) {
            logger.error("Error saving FormularioDoacaoMensal: {}", e.getMessage(), e);
            throw e;
        }
    }
}