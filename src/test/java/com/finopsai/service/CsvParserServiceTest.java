package com.finopsai.service;

import com.finopsai.exception.InvalidCsvException;
import com.finopsai.model.Upload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CsvParserServiceTest {

    @Autowired
    private CsvParserService csvParserService;

    @Test
    void parsesAndStoresValidCsv() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.csv",
                "text/csv",
                """
                        Service,MonthlyCost,CPUUsage
                        EC2-t3.medium,50,12
                        EC2-m5.large,120,85
                        Lambda,15,45
                        """.getBytes()
        );

        Upload upload = csvParserService.parseAndSave(file);

        assertThat(upload.getId()).isNotNull();
        assertThat(upload.getResources()).hasSize(3);
        assertThat(upload.getResources().get(0).getRecommendation().getEstimatedSavings()).isEqualByComparingTo("20.00");
    }

    @Test
    void rejectsInvalidHeader() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.csv",
                "text/csv",
                """
                        Name,Cost,Usage
                        EC2,50,12
                        """.getBytes()
        );

        assertThatThrownBy(() -> csvParserService.parseAndSave(file))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("CSV header");
    }
}
