package com.ong.api_backend.model;

public record EmailDashboardRequest(

        String email,
        String assunto,
        String mensagem,
        String nomeFormulario,
        String nome,
        String mensagemOriginal
) {

}
