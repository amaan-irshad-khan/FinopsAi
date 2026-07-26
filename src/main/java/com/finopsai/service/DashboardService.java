package com.finopsai.service;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.model.ResourceUsage;
import com.finopsai.model.Upload;
import com.finopsai.repository.ResourceUsageRepository;
import com.finopsai.repository.UploadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Upload> recentUploads() {
        return uploadRepository.findTop10ByOrderByUploadedAtDesc();
    }

    public Upload latestUpload() {
        return recentUploads().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No uploads available yet."));
    }

    public Upload upload(Long uploadId) {
        return uploadRepository.findById(uploadId)
                .orElseThrow(() -> new ResourceNotFoundException("Upload not found: " + uploadId));
    }

    public List<ResourceUsage> resources(Long uploadId) {
        return resourceUsageRepository.findByUploadId(uploadId);
    }

    public AnalysisResponse analysis(Long uploadId) {
        upload(uploadId);
        return costAnalysisService.analyze(uploadId);
    }
}
