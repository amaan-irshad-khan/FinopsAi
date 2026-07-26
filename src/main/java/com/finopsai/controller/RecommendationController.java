package com.finopsai.controller;

import com.finopsai.dto.RecommendationResponse;
import com.finopsai.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{uploadId}")
    public List<RecommendationResponse> recommendations(@PathVariable Long uploadId) {
        return recommendationService.recommendationsForUpload(uploadId);
    }
}
