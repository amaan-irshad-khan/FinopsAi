package com.finopsai.dto;

import java.math.BigDecimal;

public record BudgetResponse(Long id, String name, String serviceName, BigDecimal monthlyLimit,
                             int alertThreshold, BigDecimal currentSpend, int usagePercent, boolean alerting) { }
