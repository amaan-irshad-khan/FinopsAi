package com.finopsai.repository;

import com.finopsai.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByResourceUploadId(Long uploadId);
}
