package com.finopsai.repository;

import com.finopsai.model.FinOpsUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<FinOpsUser, Long> {

    Optional<FinOpsUser> findByUsername(String username);

    boolean existsByUsername(String username);
}