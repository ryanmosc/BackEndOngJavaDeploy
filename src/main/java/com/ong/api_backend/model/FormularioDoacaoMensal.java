package com.ong.api_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "doacao_mensal")
public class FormularioDoacaoMensal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_completo")
    @NotEmpty(message = "Nome completo é obrigatório")
    @Size( max = 100, message = "Nome deve ter no maximo 100 caracteres")
    private String nomeCompleto;

    @Column(name = "email")
    @NotEmpty(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Column(name = "mensagem")
    @NotEmpty(message = "Mensagem é obrigatória")
    @Size(max = 500, message = "Mensagem deve ter no máximo 500 caracteres")
    private String mensagem;

}
