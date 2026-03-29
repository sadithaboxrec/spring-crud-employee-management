package com.test.employee.controller;

import com.test.employee.dto.LoginRequest;
import com.test.employee.dto.RefreshTokenRequest;
import com.test.employee.dto.RegisterRequest;
import com.test.employee.dto.TokenPair;
import com.test.employee.entity.Employee;
import com.test.employee.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        // save the new user to the database and return a suuccess response
        // next step is creating the AuthService

        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){

        // authenticate the user
        // return access token and refresh token

        TokenPair tokenPair = authService.login(loginRequest);
        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request){

        TokenPair tokenPair= authService.refreshToken(request);
        return ResponseEntity.ok(tokenPair);
    }

}
