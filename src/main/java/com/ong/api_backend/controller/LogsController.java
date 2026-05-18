package com.ong.api_backend.controller;

import com.ong.api_backend.model.Logs;
import com.ong.api_backend.service.LogsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/logs")
public class LogsController {

    private final LogsService service;

    @GetMapping
    public ResponseEntity<List<Logs>> listarLogsSomenteAdmin(){
        List<Logs> logs = service.listarLogs();
        return ResponseEntity.ok(logs);
    }
}
