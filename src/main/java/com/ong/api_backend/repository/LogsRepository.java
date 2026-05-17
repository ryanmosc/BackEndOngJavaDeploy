package com.ong.api_backend.repository;

import com.ong.api_backend.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<Logs, Long> {


}
