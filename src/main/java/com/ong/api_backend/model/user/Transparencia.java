package com.ong.api_backend.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transparencia")
public class Transparencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arquivo", columnDefinition = "BYTEA")
    private  byte[] arquivo;

    @Column(name = "nome_arquivo")
    private String texto;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;


    public Transparencia(byte[] bytes, String texto, LocalDateTime now) {
        this.arquivo = bytes;
        this.texto = texto;
        this.createdAt = now;
    }
}
