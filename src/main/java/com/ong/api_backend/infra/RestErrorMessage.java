package com.ong.api_backend.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class RestErrorMessage {
    private String message;
    private HttpStatus status;
    private Integer statusCode;
    private LocalDateTime date;
    private Map<String, String> errors;

    // Construtor sem o campo errors (para compatibilidade com outros casos)
    public RestErrorMessage(String message, HttpStatus status, Integer statusCode, LocalDateTime date) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
        this.date = date;
        this.errors = null;
    }
}