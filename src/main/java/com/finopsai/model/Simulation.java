package com.finopsai.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulations")
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scenario_name", nullable = false)
    private String scenarioName;

    @Column(name = "current_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentCost;

    @Column(name = "projected_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal projectedCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal savings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private FinOpsUser owner;

    protected Simulation() {
    }

    public Simulation(String scenarioName, BigDecimal currentCost, BigDecimal projectedCost, BigDecimal savings, FinOpsUser owner) {
        this.scenarioName = scenarioName;
        this.currentCost = currentCost;
        this.projectedCost = projectedCost;
        this.savings = savings;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public BigDecimal getCurrentCost() {
        return currentCost;
    }

    public BigDecimal getProjectedCost() {
        return projectedCost;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public FinOpsUser getOwner() {
        return owner;
    }
}
