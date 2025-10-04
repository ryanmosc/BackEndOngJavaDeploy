package com.ong.api_backend.repository;

import com.ong.api_backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findTop3ByOrderByIdDesc();
}
