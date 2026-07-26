package com.finopsai.service;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.model.ResourceStatus;
import com.finopsai.model.ResourceUsage;
import com.finopsai.repository.ResourceUsageRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CostAnalysisService {

    private static final BigDecimal LOW_CPU_THRESHOLD = BigDecimal.valueOf(20);
    private static final BigDecimal HIGH_CPU_THRESHOLD = BigDecimal.valueOf(80);
    private final ResourceUsageRepository resourceUsageRepository;

    public CostAnalysisService(ResourceUsageRepository resourceUsageRepository) {
        this.resourceUsageRepository = resourceUsageRepository;
    }

    public ResourceStatus classify(BigDecimal cpuUsage) {
        if (cpuUsage.compareTo(LOW_CPU_THRESHOLD) < 0) {
            return ResourceStatus.UNDERUTILIZED;
        }
        if (cpuUsage.compareTo(HIGH_CPU_THRESHOLD) > 0) {
            return ResourceStatus.OVERLOADED;
        }
        return ResourceStatus.HEALTHY;
    }

    public AnalysisResponse analyze(Long uploadId) {
        List<ResourceUsage> resources = resourceUsageRepository.findByUploadId(uploadId);
        BigDecimal currentCost = totalCost(resources);
        BigDecimal estimatedSavings = resources.stream()
                .map(resource -> resource.getRecommendation() == null
                        ? BigDecimal.ZERO
                        : resource.getRecommendation().getEstimatedSavings())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal optimizedCost = currentCost.subtract(estimatedSavings).max(BigDecimal.ZERO);
        int optimizationScore = currentCost.compareTo(BigDecimal.ZERO) == 0
                ? 100
                : estimatedSavings.multiply(BigDecimal.valueOf(100))
                .divide(currentCost, 0, RoundingMode.HALF_UP)
                .intValue();

        return new AnalysisResponse(
                uploadId,
                currentCost,
                estimatedSavings,
                optimizedCost,
                optimizationScore,
                countByStatus(resources, ResourceStatus.UNDERUTILIZED),
                countByStatus(resources, ResourceStatus.OVERLOADED),
                countByStatus(resources, ResourceStatus.HEALTHY)
        );
    }

    public BigDecimal totalCost(List<ResourceUsage> resources) {
        return resources.stream()
                .map(ResourceUsage::getMonthlyCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long countByStatus(List<ResourceUsage> resources, ResourceStatus status) {
        return resources.stream()
                .filter(resource -> resource.getStatus() == status)
                .count();
    }
}
