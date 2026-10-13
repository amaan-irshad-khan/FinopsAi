package com.finopsai.service;

import com.finopsai.dto.SimulationResponse;
import com.finopsai.model.ResourceUsage;
import com.finopsai.model.Simulation;
import com.finopsai.repository.ResourceUsageRepository;
import com.finopsai.repository.SimulationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SimulationService {

    private final ResourceUsageRepository resourceUsageRepository;
    private final SimulationRepository simulationRepository;
    private final CostAnalysisService costAnalysisService;
    private final CurrentUserService currentUserService;

    public SimulationService(
            ResourceUsageRepository resourceUsageRepository,
            SimulationRepository simulationRepository,
            CostAnalysisService costAnalysisService,
            CurrentUserService currentUserService
    ) {
        this.resourceUsageRepository = resourceUsageRepository;
        this.simulationRepository = simulationRepository;
        this.costAnalysisService = costAnalysisService;
        this.currentUserService = currentUserService;
    }

    public SimulationResponse simulate(String scenario, String username) {
        List<ResourceUsage> resources = resourceUsageRepository.findByUploadOwnerUsername(username);
        BigDecimal currentCost = costAnalysisService.totalCost(resources);
        BigDecimal projectedCost = projectedCost(currentCost, scenario);
        BigDecimal savings = currentCost.subtract(projectedCost).max(BigDecimal.ZERO);

        simulationRepository.save(new Simulation(scenario, currentCost, projectedCost, savings, currentUserService.requireUser(username)));
        return new SimulationResponse(scenario, currentCost, projectedCost, savings);
    }

    public List<Simulation> recentSimulations(String username) {
        return simulationRepository.findTop10ByOwnerUsernameOrderByCreatedAtDesc(username);
    }

    private BigDecimal projectedCost(BigDecimal currentCost, String scenario) {
        String normalized = scenario == null ? "" : scenario.toLowerCase();
        int savingsPercent;
        if (normalized.contains("lambda") || normalized.contains("serverless")) {
            savingsPercent = 35;
        } else if (normalized.contains("reserved")) {
            savingsPercent = 25;
        } else if (normalized.contains("storage") || normalized.contains("s3")) {
            savingsPercent = 15;
        } else {
            savingsPercent = 20;
        }

        BigDecimal savings = currentCost.multiply(BigDecimal.valueOf(savingsPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return currentCost.subtract(savings).max(BigDecimal.ZERO);
    }
}
