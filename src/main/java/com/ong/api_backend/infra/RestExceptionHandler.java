package com.ong.api_backend.infra;

import com.ong.api_backend.exceptions.AuthenticationException;
import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<RestErrorMessage> dadosInvalidos(DadosInvalidosException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    @ExceptionHandler(DadosNaoEncontrados.class)
    public ResponseEntity<RestErrorMessage> dadosNaoEncontrados(DadosNaoEncontrados exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestErrorMessage> dadosInvalidos(AuthenticationException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        RestErrorMessage errorResponse = new RestErrorMessage(
                "Erro de validação",
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}