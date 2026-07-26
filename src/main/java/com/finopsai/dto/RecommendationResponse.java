package com.finopsai.dto;

import java.math.BigDecimal;

public record RecommendationResponse(
        String service,
        String recommendation,
        BigDecimal saving
) {
}
