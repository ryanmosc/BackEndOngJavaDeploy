package com.ong.api_backend.model;

public record EmailDashboardRequest(

        String email,
        String assunto,
        String mensagem
) {

}
