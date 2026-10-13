package com.finopsai.repository;

import com.finopsai.model.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    List<Simulation> findTop10ByOwnerUsernameOrderByCreatedAtDesc(String username);
}
