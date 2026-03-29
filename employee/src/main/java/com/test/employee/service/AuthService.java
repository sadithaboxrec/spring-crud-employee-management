package com.test.employee.service;

import com.test.employee.dto.LoginRequest;
import com.test.employee.dto.RefreshTokenRequest;
import com.test.employee.dto.RegisterRequest;
import com.test.employee.dto.TokenPair;
import com.test.employee.entity.User;
import com.test.employee.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    // next creating SecurityConfig class and Password encrypt

    // for TokenPair login authentication
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // after refresh token method

    private final UserDetailsService userDetailsService;

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


    // after JwtService and filter

    public TokenPair login(LoginRequest loginRequest) {

        // authentication the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Set Authentication in Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate Token Pair
//        TokenPair tokenPair = jwtService.generateTokenPair(authentication);
//        return tokenPair;

        return jwtService.generateTokenPair(authentication);

    }



    // refreshToken

    public TokenPair refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {

        String refreshToken=refreshTokenRequest.getRefreshToken();
        // check if it is valid refresh token

        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if(userDetails==null) {
            throw new IllegalArgumentException("User not found");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtService.generateAccessToken(authenticationToken);
        return new TokenPair(accessToken, refreshToken);

    }
}
