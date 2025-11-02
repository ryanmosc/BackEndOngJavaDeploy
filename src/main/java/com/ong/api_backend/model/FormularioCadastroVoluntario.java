package com.ong.api_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "seja_voluntario")
public class FormularioCadastroVoluntario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome_completo;

    @Column(name = "email")
    @NotEmpty(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String e_mail;

    private Long telefone;

    @Column(name = "disponibilidade")
    private String disponibilidade;

    @Column(name = "mensagem")
    @NotEmpty(message = "Mensagem é obrigatória")
    @Size(min = 10, max = 500, message = "Mensagem deve ter entre 10 e 500 caracteres")
    private String mensagem;

}
