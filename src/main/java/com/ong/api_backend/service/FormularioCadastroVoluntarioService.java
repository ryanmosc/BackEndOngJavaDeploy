package com.ong.api_backend.service;

import com.ong.api_backend.dao.FormularioCadastroVoluntarioDao;
import com.ong.api_backend.model.FormularioCadastroVoluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormularioCadastroVoluntarioService {
    @Autowired
    private FormularioCadastroVoluntarioDao formularioCadastroVoluntarioDao;

    //Create Formulario
    public void saveAllFormularioCadastroVoluntariosService(FormularioCadastroVoluntario formularioCadastroVoluntario){
        formularioCadastroVoluntarioDao.saveFormularioCadastroVoluntario(formularioCadastroVoluntario);
    }
}
