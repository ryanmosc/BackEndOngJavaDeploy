package com.ong.api_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
    @Entity
    @Data
    @Table(name = "atualizacoes")
    public class Atualizacoes {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @NotEmpty
        @Column(nullable = false)
        private String texto;

        @NotEmpty
        private String imagem;

        @Column(updatable = false)
        private LocalDateTime dataCriacao = LocalDateTime.now();
    }


