package com.finopsai.dto;

import java.math.BigDecimal;

public record SimulationResponse(
        String scenario,
        BigDecimal currentCost,
        BigDecimal newCost,
        BigDecimal saving
) {
}
