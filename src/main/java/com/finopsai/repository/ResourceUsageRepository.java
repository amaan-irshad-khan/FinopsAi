package com.finopsai.repository;

import com.finopsai.model.ResourceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceUsageRepository extends JpaRepository<ResourceUsage, Long> {

    List<ResourceUsage> findByUploadId(Long uploadId);
}
