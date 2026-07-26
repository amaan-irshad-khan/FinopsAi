package com.finopsai.service;

import com.finopsai.model.ResourceStatus;
import com.finopsai.repository.ResourceUsageRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CostAnalysisServiceTest {

    private final CostAnalysisService service = new CostAnalysisService(mock(ResourceUsageRepository.class));

    @Test
    void classifiesUnderutilizedResources() {
        assertThat(service.classify(BigDecimal.valueOf(19))).isEqualTo(ResourceStatus.UNDERUTILIZED);
    }

    @Test
    void classifiesHealthyResources() {
        assertThat(service.classify(BigDecimal.valueOf(20))).isEqualTo(ResourceStatus.HEALTHY);
        assertThat(service.classify(BigDecimal.valueOf(80))).isEqualTo(ResourceStatus.HEALTHY);
    }

    @Test
    void classifiesOverloadedResources() {
        assertThat(service.classify(BigDecimal.valueOf(81))).isEqualTo(ResourceStatus.OVERLOADED);
    }
}
