package com.finopsai.repository;

import com.finopsai.model.Upload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadRepository extends JpaRepository<Upload, Long> {

    List<Upload> findTop10ByOwnerUsernameOrderByUploadedAtDesc(String username);

    Page<Upload> findAllByOwnerUsernameOrderByUploadedAtDesc(String username, Pageable pageable);

    java.util.Optional<Upload> findByIdAndOwnerUsername(Long id, String username);
}
