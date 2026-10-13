package com.finopsai.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "uploads")
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private FinOpsUser owner;

    @OneToMany(mappedBy = "upload", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceUsage> resources = new ArrayList<>();

    protected Upload() {
    }

    public Upload(String filename, FinOpsUser owner) {
        this.filename = filename;
        this.owner = owner;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public FinOpsUser getOwner() {
        return owner;
    }

    public List<ResourceUsage> getResources() {
        return resources;
    }

    public void addResource(ResourceUsage resource) {
        resources.add(resource);
        resource.setUpload(this);
    }
}
