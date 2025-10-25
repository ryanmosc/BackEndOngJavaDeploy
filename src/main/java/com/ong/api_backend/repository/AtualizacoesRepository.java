package com.ong.api_backend.repository;

import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtualizacoesRepository extends JpaRepository<Atualizacoes, Integer> {
    List<Atualizacoes> findTop3ByOrderByIdDesc();
}
