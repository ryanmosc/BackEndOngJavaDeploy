package com.ong.api_backend.service;

import com.ong.api_backend.model.Logs;
import com.ong.api_backend.repository.LogsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogsService {
    private final LogsRepository repository;

    public void salvarLog(Logs logs){
        repository.save(logs);
    }
}
