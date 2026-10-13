package com.finopsai.dto;

import java.math.BigDecimal;

public record ComparisonResponse(Long baselineUploadId, Long currentUploadId, BigDecimal baselineCost,
                                 BigDecimal currentCost, BigDecimal costChange, int costChangePercent,
                                 long baselineResources, long currentResources) { }
