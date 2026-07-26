package com.finopsai.controller;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DashboardService dashboardService;

    public AnalysisController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{uploadId}")
    public AnalysisResponse analysis(@PathVariable Long uploadId) {
        return dashboardService.analysis(uploadId);
    }
}
