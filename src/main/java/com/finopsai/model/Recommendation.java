package com.finopsai.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

import java.math.BigDecimal;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceUsage resource;

    @Column(nullable = false)
    private String recommendation;

    @Column(name = "estimated_savings", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedSavings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecommendationState state = RecommendationState.OPEN;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Recommendation() {
    }

    public Recommendation(String recommendation, BigDecimal estimatedSavings) {
        this.recommendation = recommendation;
        this.estimatedSavings = estimatedSavings;
    }

    public Long getId() {
        return id;
    }

    public ResourceUsage getResource() {
        return resource;
    }

    public void setResource(ResourceUsage resource) {
        this.resource = resource;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public BigDecimal getEstimatedSavings() {
        return estimatedSavings;
    }

    public RecommendationState getState() {
        return state;
    }

    public void setState(RecommendationState state) {
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }
}
