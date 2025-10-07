package com.ong.api_backend.service;

import com.ong.api_backend.dao.FaleConoscoDao;
import com.ong.api_backend.model.FaleConosco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaleConoscoService {
    private static final Logger logger = LoggerFactory.getLogger(FaleConoscoService.class);

    @Autowired
    private FaleConoscoDao faleConoscoDao;

    public FaleConoscoService(FaleConoscoDao faleConoscoDao) {
        this.faleConoscoDao = faleConoscoDao;
        logger.debug("FaleConoscoService initialized");
    }

    public void saveAllService(FaleConosco faleConosco) {
        logger.info("Attempting to save FaleConosco message");
        try {
            faleConoscoDao.saveFaleConosco(faleConosco);
            logger.info("FaleConosco message saved successfully");
        } catch (Exception e) {
            logger.error("Error saving FaleConosco message: {}", e.getMessage(), e);
            throw e;
        }
    }
}