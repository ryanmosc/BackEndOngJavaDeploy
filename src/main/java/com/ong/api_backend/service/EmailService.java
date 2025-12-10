package com.ong.api_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente = "contato@voluntariosdasaude.com.br";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String para, String assunto, String corpo) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom(remetente); // garante que o remetente seja reconhecido pelo SMTP
        mensagem.setTo(para);
        mensagem.setSubject(assunto);
        mensagem.setText(corpo);

        mailSender.send(mensagem);
    }
}
