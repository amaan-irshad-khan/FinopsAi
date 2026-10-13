package com.finopsai.service;

import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.model.FinOpsUser;
import com.finopsai.model.Upload;
import com.finopsai.repository.UploadRepository;
import com.finopsai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
class DashboardOwnershipTest {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserRepository userRepository;
    @Autowired private UploadRepository uploadRepository;

    @Test
    void doesNotReturnAnotherUsersUpload() {
        FinOpsUser owner = userRepository.save(new FinOpsUser("owner-test", "password", "VIEWER"));
        userRepository.save(new FinOpsUser("other-test", "password", "VIEWER"));
        Upload upload = uploadRepository.save(new Upload("private.csv", owner));

        assertThatThrownBy(() -> dashboardService.upload(upload.getId(), "other-test"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
