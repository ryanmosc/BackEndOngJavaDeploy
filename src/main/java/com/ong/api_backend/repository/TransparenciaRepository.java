package com.ong.api_backend.repository;

import com.ong.api_backend.model.user.Transparencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransparenciaRepository extends JpaRepository <Transparencia, Long> {
    Optional<Transparencia> findTopByOrderByCreatedAtDesc();
}
