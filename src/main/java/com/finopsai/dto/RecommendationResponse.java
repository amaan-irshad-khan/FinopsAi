package com.finopsai.dto;

import java.math.BigDecimal;
import com.finopsai.model.RecommendationState;

public record RecommendationResponse(
        Long id,
        String service,
        String recommendation,
        BigDecimal saving,
        RecommendationState state
) {
}
