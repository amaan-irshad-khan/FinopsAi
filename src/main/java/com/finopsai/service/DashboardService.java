package com.finopsai.service;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.model.ResourceUsage;
import com.finopsai.model.Upload;
import com.finopsai.repository.ResourceUsageRepository;
import com.finopsai.repository.UploadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.finopsai.dto.ComparisonResponse;
import com.finopsai.dto.ServiceSpend;

@Service
public class DashboardService {

    private final UploadRepository uploadRepository;
    private final ResourceUsageRepository resourceUsageRepository;
    private final CostAnalysisService costAnalysisService;

    public DashboardService(
            UploadRepository uploadRepository,
            ResourceUsageRepository resourceUsageRepository,
            CostAnalysisService costAnalysisService
    ) {
        this.uploadRepository = uploadRepository;
        this.resourceUsageRepository = resourceUsageRepository;
        this.costAnalysisService = costAnalysisService;
    }

    public List<Upload> recentUploads(String username) {
        return uploadRepository.findTop10ByOwnerUsernameOrderByUploadedAtDesc(username);
    }

    public Upload latestUpload(String username) {
        return recentUploads(username).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No uploads available yet."));
    }

    public Upload upload(Long uploadId, String username) {
        return uploadRepository.findByIdAndOwnerUsername(uploadId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Upload not found: " + uploadId));
    }

    public List<ResourceUsage> resources(Long uploadId, String username) {
        return resourceUsageRepository.findByUploadIdAndUploadOwnerUsername(uploadId, username);
    }

    public Page<ResourceUsage> paginatedResources(Long uploadId, Pageable pageable, String username) {
        return resourceUsageRepository.findByUploadIdAndUploadOwnerUsername(uploadId, username, pageable);
    }

    public AnalysisResponse analysis(Long uploadId, String username) {
        upload(uploadId, username);
        return costAnalysisService.analyze(uploadId);
    }

    public ComparisonResponse compare(Long baselineId, Long currentId, String username) {
        Upload baseline = upload(baselineId, username);
        Upload current = upload(currentId, username);
        BigDecimal baselineCost = costAnalysisService.totalCost(resources(baseline.getId(), username));
        BigDecimal currentCost = costAnalysisService.totalCost(resources(current.getId(), username));
        BigDecimal change = currentCost.subtract(baselineCost);
        int changePercent = baselineCost.signum() == 0 ? 0 : change.multiply(BigDecimal.valueOf(100)).divide(baselineCost, 0, RoundingMode.HALF_UP).intValue();
        return new ComparisonResponse(baselineId, currentId, baselineCost, currentCost, change, changePercent,
                resources(baselineId, username).size(), resources(currentId, username).size());
    }

    public List<ServiceSpend> serviceSpend(Long uploadId, String username) {
        List<ResourceUsage> resources = resources(uploadId, username);
        BigDecimal total = costAnalysisService.totalCost(resources);
        return resources.stream().collect(java.util.stream.Collectors.groupingBy(ResourceUsage::getServiceName,
                        java.util.stream.Collectors.reducing(BigDecimal.ZERO, ResourceUsage::getMonthlyCost, BigDecimal::add)))
                .entrySet().stream().sorted(java.util.Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new ServiceSpend(entry.getKey(), entry.getValue(), total.signum() == 0 ? 0 : entry.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 0, RoundingMode.HALF_UP).intValue()))
                .toList();
    }
}
