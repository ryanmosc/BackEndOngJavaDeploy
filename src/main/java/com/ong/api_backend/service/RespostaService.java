package com.ong.api_backend.service;

import com.ong.api_backend.model.Resposta;
import com.ong.api_backend.repository.RespostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RespostaService {

    @Autowired
    private RespostaRepository respostaRepository;

    public void registrar(
            String nomeFormulario,
            String nome,
            String email,
            String mensagemOriginal,
            String respostaEnviada) {

        Resposta r = Resposta.builder()
                .nomeFormulario(nomeFormulario)
                .nome(nome)
                .email(email)
                .mensagemOriginal(mensagemOriginal)
                .respostaEnviada(respostaEnviada)
                .data(LocalDateTime.now())
                .build();

        respostaRepository.save(r);
    }


}
