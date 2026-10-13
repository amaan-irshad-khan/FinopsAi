package com.finopsai.controller;

import com.finopsai.dto.AnalysisResponse;
import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.security.Principal;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        try {
            return "redirect:/dashboard/" + dashboardService.latestUpload(principal.getName()).getId();
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("uploads", dashboardService.recentUploads(principal.getName()));
            model.addAttribute("emptyMessage", "Upload a CSV report to generate your first dashboard.");
            return "dashboard";
        }
    }

    @GetMapping("/dashboard/{uploadId}")
    public String dashboardForUpload(@PathVariable Long uploadId, Model model, Principal principal) {
        AnalysisResponse analysis = dashboardService.analysis(uploadId, principal.getName());
        model.addAttribute("upload", dashboardService.upload(uploadId, principal.getName()));
        model.addAttribute("analysis", analysis);
        model.addAttribute("resources", dashboardService.resources(uploadId, principal.getName()));
        model.addAttribute("serviceSpend", dashboardService.serviceSpend(uploadId, principal.getName()));
        model.addAttribute("uploads", dashboardService.recentUploads(principal.getName()));
        return "dashboard";
    }

    @GetMapping("/compare")
    public String compare(@RequestParam Long baselineId, @RequestParam Long currentId, Model model, Principal principal) {
        model.addAttribute("comparison", dashboardService.compare(baselineId, currentId, principal.getName()));
        model.addAttribute("uploads", dashboardService.recentUploads(principal.getName()));
        return "comparison";
    }
}
