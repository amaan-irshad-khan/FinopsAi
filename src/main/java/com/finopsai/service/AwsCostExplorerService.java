package com.finopsai.service;

import com.finopsai.exception.InvalidCsvException;
import com.finopsai.model.*;
import com.finopsai.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AwsCostExplorerService {
    private final boolean enabled;
    private final CurrentUserService currentUserService;
    private final CostAnalysisService costAnalysisService;
    private final RecommendationService recommendationService;
    private final UploadRepository uploadRepository;

    public AwsCostExplorerService(@Value("${finops.aws.enabled:false}") boolean enabled, CurrentUserService currentUserService,
                                  CostAnalysisService costAnalysisService, RecommendationService recommendationService, UploadRepository uploadRepository) {
        this.enabled = enabled; this.currentUserService = currentUserService; this.costAnalysisService = costAnalysisService;
        this.recommendationService = recommendationService; this.uploadRepository = uploadRepository;
    }

    @Transactional
    public Upload importCurrentMonth(String username) {
        if (!enabled) { throw new InvalidCsvException("AWS import is disabled. Set FINOPS_AWS_ENABLED=true and configure AWS credentials to enable it."); }
        LocalDate end = LocalDate.now().plusDays(1);
        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder().timePeriod(DateInterval.builder().start(end.withDayOfMonth(1).toString()).end(end.toString()).build())
                .granularity(Granularity.MONTHLY).metrics("UnblendedCost")
                .groupBy(GroupDefinition.builder().type(GroupDefinitionType.DIMENSION).key("SERVICE").build()).build();
        Upload upload = new Upload("AWS Cost Explorer " + LocalDate.now(), currentUserService.requireUser(username));
        try (CostExplorerClient client = CostExplorerClient.create()) {
            client.getCostAndUsage(request).resultsByTime().forEach(result -> result.groups().forEach(group -> {
                BigDecimal cost = new BigDecimal(group.metrics().get("UnblendedCost").amount());
                if (cost.signum() > 0) {
                    ResourceUsage resource = new ResourceUsage(group.keys().get(0), cost, BigDecimal.valueOf(50), ResourceStatus.HEALTHY);
                    resource.setRecommendation(recommendationService.generate(resource));
                    upload.addResource(resource);
                }
            }));
        } catch (Exception ex) { throw new InvalidCsvException("AWS Cost Explorer import failed. Verify credentials, billing permissions, and region access."); }
        if (upload.getResources().isEmpty()) { throw new InvalidCsvException("AWS Cost Explorer returned no billable services for the current month."); }
        return uploadRepository.save(upload);
    }
}
