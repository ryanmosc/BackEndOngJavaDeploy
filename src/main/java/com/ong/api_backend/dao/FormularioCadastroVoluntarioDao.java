package com.ong.api_backend.dao;

import com.ong.api_backend.model.FormularioCadastroVoluntario;
import com.ong.api_backend.repository.FormularioCadastroVoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;


@Repository
public class FormularioCadastroVoluntarioDao {
    @Autowired
    private FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository;

    public void saveFormularioCadastroVoluntario(FormularioCadastroVoluntario formularioCadastroVoluntario){
       try {
           formularioCadastroVoluntarioRepository.saveAndFlush(formularioCadastroVoluntario);
       } catch (Exception e) {
           throw new RuntimeException("Erro no DAO");
       }
    }
}
