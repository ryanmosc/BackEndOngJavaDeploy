package com.ong.api_backend.exceptions;

public class DadosInvalidosException extends RuntimeException{
    public DadosInvalidosException(){
        super("Dados Invalidos ou faltantes");
    }
    public DadosInvalidosException(String message){
        super(message);
    }
}
