package com.ong.api_backend.dao;

import com.ong.api_backend.model.FaleConosco;
import com.ong.api_backend.repository.FaleConoscoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FaleConoscoDao {
    @Autowired
    private FaleConoscoRepository faleConoscoRepository;

    public void saveFaleConosco(FaleConosco faleConosco){
        try {
            faleConoscoRepository.saveAndFlush(faleConosco);
        }
        catch (Exception e){
            throw  new RuntimeException("Erro no DAO ");
        }

    }
}
