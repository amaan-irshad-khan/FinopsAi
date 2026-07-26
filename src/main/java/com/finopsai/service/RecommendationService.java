package com.finopsai.service;

import com.finopsai.dto.RecommendationResponse;
import com.finopsai.model.Recommendation;
import com.finopsai.model.ResourceStatus;
import com.finopsai.model.ResourceUsage;
import com.finopsai.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public Recommendation generate(ResourceUsage resource) {
        if (resource.getStatus() == ResourceStatus.UNDERUTILIZED) {
            return new Recommendation(
                    recommendationForUnderutilized(resource.getServiceName()),
                    percentage(resource.getMonthlyCost(), 40)
            );
        }
        if (resource.getStatus() == ResourceStatus.OVERLOADED) {
            return new Recommendation(
                    "Scale up or distribute load to avoid performance risk",
                    BigDecimal.ZERO
            );
        }
        return new Recommendation("No action required", BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> recommendationsForUpload(Long uploadId) {
        return recommendationRepository.findByResourceUploadId(uploadId).stream()
                .map(recommendation -> new RecommendationResponse(
                        recommendation.getResource().getServiceName(),
                        recommendation.getRecommendation(),
                        recommendation.getEstimatedSavings()
                ))
                .toList();
    }

    private String recommendationForUnderutilized(String serviceName) {
        String normalized = serviceName.toLowerCase();
        if (normalized.contains("t3.medium")) {
            return "Move to t3.small";
        }
        if (normalized.contains("rds")) {
            return "Downsize database instance or use reserved capacity";
        }
        if (normalized.contains("s3")) {
            return "Review storage class and lifecycle rules";
        }
        return "Right-size resource based on actual utilization";
    }

    private BigDecimal percentage(BigDecimal amount, int percent) {
        return amount.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
