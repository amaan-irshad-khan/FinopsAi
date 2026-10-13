package com.finopsai.controller;

import com.finopsai.dto.RecommendationResponse;
import com.finopsai.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.finopsai.model.RecommendationState;
import java.security.Principal;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @ResponseBody @GetMapping("/{uploadId}")
    public List<RecommendationResponse> recommendations(@PathVariable Long uploadId, Principal principal) {
        return recommendationService.recommendationsForUpload(uploadId, principal.getName());
    }

    @ResponseBody @PatchMapping("/{recommendationId}/state")
    public Map<String, RecommendationState> updateState(@PathVariable Long recommendationId,
                                                          @RequestBody Map<String, RecommendationState> request,
                                                          Principal principal) {
        RecommendationState state = request.get("state");
        if (state == null) { throw new IllegalArgumentException("state is required."); }
        return Map.of("state", recommendationService.updateState(recommendationId, state, principal.getName()));
    }

    @PostMapping("/manage/{recommendationId}")
    public String updateStateFromPage(@PathVariable Long recommendationId, @RequestParam RecommendationState state,
                                      @RequestParam Long uploadId, Principal principal, RedirectAttributes flash) {
        recommendationService.updateState(recommendationId, state, principal.getName());
        flash.addFlashAttribute("success", "Recommendation status updated.");
        return "redirect:/dashboard/" + uploadId;
    }
}
