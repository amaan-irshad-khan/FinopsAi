package com.finopsai.service;

import com.finopsai.exception.ResourceNotFoundException;
import com.finopsai.model.FinOpsUser;
import com.finopsai.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;
    public CurrentUserService(UserRepository userRepository) { this.userRepository = userRepository; }
    public FinOpsUser requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user was not found."));
    }
}
