package com.finopsai.repository;

import com.finopsai.model.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadRepository extends JpaRepository<Upload, Long> {

    List<Upload> findTop10ByOrderByUploadedAtDesc();
}
