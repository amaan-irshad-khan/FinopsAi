package com.finopsai.controller;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            return "redirect:/dashboard/" + dashboardService.latestUpload().getId();
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("uploads", dashboardService.recentUploads());
            model.addAttribute("emptyMessage", "Upload a CSV report to generate your first dashboard.");
            return "dashboard";
        }
    }

    @GetMapping("/dashboard/{uploadId}")
    public String dashboardForUpload(@PathVariable Long uploadId, Model model) {
        AnalysisResponse analysis = dashboardService.analysis(uploadId);
        model.addAttribute("upload", dashboardService.upload(uploadId));
        model.addAttribute("analysis", analysis);
        model.addAttribute("resources", dashboardService.resources(uploadId));
        model.addAttribute("uploads", dashboardService.recentUploads());
        return "dashboard";
    }
}
