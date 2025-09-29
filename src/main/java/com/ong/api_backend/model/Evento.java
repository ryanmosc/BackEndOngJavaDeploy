package com.ong.api_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "eventos")
public class Evento {
    @Id  // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
    private Integer id;

    @Column(nullable = false)  // Não pode ser NULL
    private String texto;

    private String imagem;  // Path da imagem (pode ser NULL se não houver imagem)

    @Column(updatable = false)  // Não atualiza após insert
    private LocalDateTime dataCriacao = LocalDateTime.now();  // Data automática
}
