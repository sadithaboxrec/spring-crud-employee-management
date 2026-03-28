package com.test.employee.service;

import com.test.employee.dto.RegisterRequest;
import com.test.employee.entity.User;
import com.test.employee.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    // next creating SecurityConfig class and Password encrypt

    @Transactional  // SINCE WE DOING DATABASE operations it is better to have this so it can help prevent exceptions
    public void registerUser(RegisterRequest registerRequest) {

        // Check if user with the same username already exist

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = User
                .builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();

        userRepository.save(user);
    }





}
