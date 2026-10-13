package com.finopsai.service;

import com.finopsai.dto.BudgetRequest;
import com.finopsai.dto.BudgetResponse;
import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.model.Budget;
import com.finopsai.repository.BudgetRepository;
import com.finopsai.repository.ResourceUsageRepository;
import com.finopsai.repository.UploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ResourceUsageRepository resourceUsageRepository;
    private final CurrentUserService currentUserService;
    private final UploadRepository uploadRepository;
    public BudgetService(BudgetRepository budgetRepository, ResourceUsageRepository resourceUsageRepository, CurrentUserService currentUserService, UploadRepository uploadRepository) {
        this.budgetRepository = budgetRepository; this.resourceUsageRepository = resourceUsageRepository; this.currentUserService = currentUserService; this.uploadRepository = uploadRepository;
    }
    @Transactional
    public BudgetResponse create(BudgetRequest request, String username) {
        Budget budget = budgetRepository.save(new Budget(currentUserService.requireUser(username), request.name(), request.serviceName(), request.monthlyLimit(), request.alertThreshold()));
        return response(budget, username);
    }
    @Transactional(readOnly = true)
    public List<BudgetResponse> list(String username) { return budgetRepository.findAllByOwnerUsernameOrderByCreatedAtDesc(username).stream().map(b -> response(b, username)).toList(); }
    @Transactional
    public void delete(Long id, String username) { budgetRepository.delete(budgetRepository.findByIdAndOwnerUsername(id, username).orElseThrow(() -> new ResourceNotFoundException("Budget not found."))); }
    private BudgetResponse response(Budget budget, String username) {
        BigDecimal spend = uploadRepository.findTop10ByOwnerUsernameOrderByUploadedAtDesc(username).stream().findFirst()
                .map(upload -> resourceUsageRepository.findByUploadIdAndUploadOwnerUsername(upload.getId(), username).stream()
                .filter(resource -> "All services".equalsIgnoreCase(budget.getServiceName()) || resource.getServiceName().toLowerCase().contains(budget.getServiceName().toLowerCase()))
                .map(resource -> resource.getMonthlyCost()).reduce(BigDecimal.ZERO, BigDecimal::add)).orElse(BigDecimal.ZERO);
        int percent = spend.multiply(BigDecimal.valueOf(100)).divide(budget.getMonthlyLimit(), 0, RoundingMode.HALF_UP).intValue();
        return new BudgetResponse(budget.getId(), budget.getName(), budget.getServiceName(), budget.getMonthlyLimit(), budget.getAlertThreshold(), spend, percent, percent >= budget.getAlertThreshold());
    }
}
