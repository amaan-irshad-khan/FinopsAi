package com.finopsai.repository;

import com.finopsai.model.ResourceUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceUsageRepository extends JpaRepository<ResourceUsage, Long> {

    List<ResourceUsage> findByUploadId(Long uploadId);

    List<ResourceUsage> findByUploadIdAndUploadOwnerUsername(Long uploadId, String username);

    Page<ResourceUsage> findByUploadIdAndUploadOwnerUsername(Long uploadId, String username, Pageable pageable);

    List<ResourceUsage> findByUploadOwnerUsername(String username);
}
