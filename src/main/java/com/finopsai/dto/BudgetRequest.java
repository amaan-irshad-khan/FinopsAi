package com.finopsai.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BudgetRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String serviceName,
        @NotNull @DecimalMin(value = "0.01") BigDecimal monthlyLimit,
        @Min(1) @Max(100) int alertThreshold
) { }
