package com.finopsai.service;

import com.finopsai.exception.InvalidCsvException;
import com.finopsai.model.Recommendation;
import com.finopsai.model.ResourceUsage;
import com.finopsai.model.Upload;
import com.finopsai.repository.UploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    private final CostAnalysisService costAnalysisService;
    private final RecommendationService recommendationService;
    private final UploadRepository uploadRepository;

    public CsvParserService(
            CostAnalysisService costAnalysisService,
            RecommendationService recommendationService,
            UploadRepository uploadRepository
    ) {
        this.costAnalysisService = costAnalysisService;
        this.recommendationService = recommendationService;
        this.uploadRepository = uploadRepository;
    }

    @Transactional
    public Upload parseAndSave(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidCsvException("Please upload a non-empty CSV file.");
        }
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new InvalidCsvException("Only CSV files are supported.");
        }

        List<ResourceUsage> resources = parse(file);
        Upload upload = new Upload(file.getOriginalFilename());
        resources.forEach(upload::addResource);
        return uploadRepository.save(upload);
    }

    private List<ResourceUsage> parse(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            validateHeader(header);

            List<ResourceUsage> resources = new ArrayList<>();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                resources.add(parseLine(line, lineNumber));
            }

            if (resources.isEmpty()) {
                throw new InvalidCsvException("CSV must include at least one resource row.");
            }
            return resources;
        } catch (IOException ex) {
            throw new InvalidCsvException("Unable to read uploaded CSV file.");
        }
    }

    private ResourceUsage parseLine(String line, int lineNumber) {
        String[] columns = line.split(",", -1);
        if (columns.length != 3) {
            throw new InvalidCsvException("Line " + lineNumber + " must contain Service, MonthlyCost, CPUUsage.");
        }

        String serviceName = columns[0].trim();
        BigDecimal monthlyCost = parseDecimal(columns[1], "MonthlyCost", lineNumber);
        BigDecimal cpuUsage = parseDecimal(columns[2], "CPUUsage", lineNumber);

        if (serviceName.isBlank()) {
            throw new InvalidCsvException("Line " + lineNumber + " has an empty service name.");
        }
        if (monthlyCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidCsvException("Line " + lineNumber + " has a negative monthly cost.");
        }
        if (cpuUsage.compareTo(BigDecimal.ZERO) < 0 || cpuUsage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidCsvException("Line " + lineNumber + " CPU usage must be between 0 and 100.");
        }

        ResourceUsage resource = new ResourceUsage(
                serviceName,
                monthlyCost,
                cpuUsage,
                costAnalysisService.classify(cpuUsage)
        );
        Recommendation recommendation = recommendationService.generate(resource);
        resource.setRecommendation(recommendation);
        return resource;
    }

    private BigDecimal parseDecimal(String value, String fieldName, int lineNumber) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidCsvException("Line " + lineNumber + " has invalid " + fieldName + ".");
        }
    }

    private void validateHeader(String header) {
        if (header == null) {
            throw new InvalidCsvException("CSV header is missing.");
        }
        String normalized = header.replace(" ", "").toLowerCase();
        if (!"service,monthlycost,cpuusage".equals(normalized)) {
            throw new InvalidCsvException("CSV header must be Service,MonthlyCost,CPUUsage.");
        }
    }
}
