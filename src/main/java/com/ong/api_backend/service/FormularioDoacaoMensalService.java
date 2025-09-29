package com.ong.api_backend.service;

import com.ong.api_backend.dao.FormularioDoacaoMensalDao;
import com.ong.api_backend.model.FormularioDoacaoMensal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormularioDoacaoMensalService {
    @Autowired
    private FormularioDoacaoMensalDao formularioDoacaoMensalDao;

    //Create Formulario
    public  void saveAllFormularioDoacaoMensalService(FormularioDoacaoMensal formularioDoacaoMensal){
        formularioDoacaoMensalDao.saveFormularioDoacaoMensal(formularioDoacaoMensal);
    }
}
