package com.finopsai.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id", nullable = false)
    private FinOpsUser owner;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(name = "service_name", nullable = false, length = 120)
    private String serviceName;
    @Column(name = "monthly_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyLimit;
    @Column(name = "alert_threshold", nullable = false)
    private int alertThreshold;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Budget() { }
    public Budget(FinOpsUser owner, String name, String serviceName, BigDecimal monthlyLimit, int alertThreshold) {
        this.owner = owner; this.name = name; this.serviceName = serviceName; this.monthlyLimit = monthlyLimit; this.alertThreshold = alertThreshold;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getServiceName() { return serviceName; }
    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public int getAlertThreshold() { return alertThreshold; }
}
