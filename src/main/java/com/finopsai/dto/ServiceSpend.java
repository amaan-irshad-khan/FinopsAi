package com.finopsai.dto;

import java.math.BigDecimal;

public record ServiceSpend(String service, BigDecimal cost, int percent) { }
