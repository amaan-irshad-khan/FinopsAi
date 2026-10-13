package com.finopsai.repository;

import com.finopsai.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByResourceUploadIdAndResourceUploadOwnerUsername(Long uploadId, String username);

    java.util.Optional<Recommendation> findByIdAndResourceUploadOwnerUsername(Long id, String username);
}
