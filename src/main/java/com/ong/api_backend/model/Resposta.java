package com.ong.api_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "respostas_formulario")
public class Resposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomeFormulario;
    private String nome;
    private String email;


    @Column(nullable = false)
    private Long id_pergunta;

    @Column(columnDefinition = "TEXT")
    private String mensagemOriginal;

    @Column(columnDefinition = "TEXT")
    private String respostaEnviada;

    @Column(updatable = false)
    private LocalDateTime data = LocalDateTime.now();
}
