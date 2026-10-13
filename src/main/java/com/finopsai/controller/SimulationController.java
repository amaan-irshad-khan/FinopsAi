package com.finopsai.controller;

import com.finopsai.dto.SimulationRequest;
import com.finopsai.dto.SimulationResponse;
import com.finopsai.service.SimulationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/simulation")
    public String simulation(Model model, Principal principal) {
        model.addAttribute("simulations", simulationService.recentSimulations(principal.getName()));
        return "simulation";
    }

    @PostMapping("/simulation")
    public String runSimulation(@RequestParam String scenario, RedirectAttributes redirectAttributes, Principal principal) {
        SimulationResponse response = simulationService.simulate(scenario, principal.getName());
        redirectAttributes.addFlashAttribute("simulationResult", response);
        return "redirect:/simulation";
    }

    @PostMapping("/api/simulation")
    @ResponseBody
    public SimulationResponse simulateApi(@Valid @RequestBody SimulationRequest request, Principal principal) {
        return simulationService.simulate(request.scenario(), principal.getName());
    }
}
