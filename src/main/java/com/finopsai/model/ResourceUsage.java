package com.finopsai.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "resource_usage")
public class ResourceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_id", nullable = false)
    private Upload upload;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "monthly_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyCost;

    @Column(name = "cpu_usage", nullable = false, precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    @OneToOne(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private Recommendation recommendation;

    protected ResourceUsage() {
    }

    public ResourceUsage(String serviceName, BigDecimal monthlyCost, BigDecimal cpuUsage, ResourceStatus status) {
        this.serviceName = serviceName;
        this.monthlyCost = monthlyCost;
        this.cpuUsage = cpuUsage;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getMonthlyCost() {
        return monthlyCost;
    }

    public BigDecimal getCpuUsage() {
        return cpuUsage;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
        recommendation.setResource(this);
    }
}
