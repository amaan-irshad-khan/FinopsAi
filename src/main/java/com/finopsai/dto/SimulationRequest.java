package com.finopsai.dto;

import jakarta.validation.constraints.NotBlank;

public record SimulationRequest(@NotBlank String scenario) {
}
