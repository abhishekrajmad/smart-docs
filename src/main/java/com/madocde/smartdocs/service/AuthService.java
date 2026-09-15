package com.madocde.smartdocs.service;

import com.madocde.smartdocs.dto.LoginRequest;
import com.madocde.smartdocs.dto.RegisterRequest;
import com.madocde.smartdocs.entity.User;
import com.madocde.smartdocs.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest registerRequest) {

        if(registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if(registerRequest.getPassword() == null || registerRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(OffsetDateTime.now());

        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtService.generateToken(user.getId(), user.getEmail());
    }
}
