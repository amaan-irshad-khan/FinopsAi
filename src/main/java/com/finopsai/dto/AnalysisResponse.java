package com.finopsai.dto;

import java.math.BigDecimal;

public record AnalysisResponse(
        Long uploadId,
        BigDecimal currentCost,
        BigDecimal estimatedSavings,
        BigDecimal optimizedCost,
        int optimizationScore,
        long underutilizedCount,
        long overloadedCount,
        long healthyCount
) {
}
