package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.model.Logs;
import com.ong.api_backend.repository.LogsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class LogsService {
    private final LogsRepository repository;

    public void salvarLog(HashMap<String, Object> payload){
        try {


            Logs log = new Logs();
            log.setPayload(payload);
            log.setLocalDateTime(LocalDateTime.now());
            repository.save(log);

        }catch (Exception e){
            throw  new DadosInvalidosException("Erro inesperado servidor");
        }

    }


    public List<Logs> listarLogs(){
        return repository.findAll();
    }



}
