package com.ong.api_backend.exceptions;

public class DadosNaoEncontrados extends  RuntimeException{
    public DadosNaoEncontrados(){
        super("Dados não encontrados");
    }
    public DadosNaoEncontrados(String message){
        super(message);
    }
}
