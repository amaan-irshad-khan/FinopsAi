package com.finopsai.controller;

import com.finopsai.config.JwtTokenProvider;
import com.finopsai.dto.LoginRequest;
import com.finopsai.dto.LoginResponse;
import com.finopsai.dto.RegisterRequest;
import com.finopsai.model.FinOpsUser;
import com.finopsai.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid RegisterRequest request,
            RedirectAttributes redirectAttributes
    ) {
        if (userRepository.existsByUsername(request.username())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists.");
            return "redirect:/register";
        }

        FinOpsUser user = new FinOpsUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                "VIEWER"
        );
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Account created. Please log in.");
        return "redirect:/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            String token = jwtTokenProvider.createToken(request.username());
            return ResponseEntity.ok(new LoginResponse(token, request.username()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerApi(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        FinOpsUser user = new FinOpsUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                "VIEWER"
        );
        userRepository.save(user);

        String token = jwtTokenProvider.createToken(request.username());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, request.username()));
    }
}