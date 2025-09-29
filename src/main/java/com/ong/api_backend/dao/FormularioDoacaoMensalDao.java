package com.ong.api_backend.dao;

import com.ong.api_backend.model.FormularioDoacaoMensal;
import com.ong.api_backend.repository.FormularioDoacaoMensalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormularioDoacaoMensalDao {
    @Autowired
    private FormularioDoacaoMensalRepository formularioDoacaoMensalRepository;

    public void saveFormularioDoacaoMensal(FormularioDoacaoMensal formularioDoacaoMensal){
        try {
            formularioDoacaoMensalRepository.saveAndFlush(formularioDoacaoMensal);
        } catch (Exception e) {
            throw new RuntimeException("Erro no DAO");
        }
    }
}
