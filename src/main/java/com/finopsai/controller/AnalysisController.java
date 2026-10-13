package com.finopsai.controller;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.model.ResourceUsage;
import com.finopsai.service.DashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import com.finopsai.dto.ComparisonResponse;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DashboardService dashboardService;

    public AnalysisController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{uploadId}")
    public AnalysisResponse analysis(@PathVariable Long uploadId, Principal principal) {
        return dashboardService.analysis(uploadId, principal.getName());
    }

    @GetMapping("/{uploadId}/resources")
    public Page<ResourceUsage> resources(
            @PathVariable Long uploadId,
            @PageableDefault(size = 10) Pageable pageable,
            Principal principal
    ) {
        dashboardService.upload(uploadId, principal.getName());
        return dashboardService.paginatedResources(uploadId, pageable, principal.getName());
    }

    @GetMapping("/compare")
    public ComparisonResponse compare(@RequestParam Long baselineId, @RequestParam Long currentId, Principal principal) {
        return dashboardService.compare(baselineId, currentId, principal.getName());
    }
}
