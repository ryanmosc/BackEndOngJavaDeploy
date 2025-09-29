package com.ong.api_backend.service;

import com.ong.api_backend.dao.FaleConoscoDao;
import com.ong.api_backend.model.FaleConosco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaleConoscoService {
    @Autowired
    private FaleConoscoDao faleConoscoDao;

    //Create Formulario
    public void saveAllService(FaleConosco faleConosco){

        faleConoscoDao.saveFaleConosco(faleConosco);
    }
}
