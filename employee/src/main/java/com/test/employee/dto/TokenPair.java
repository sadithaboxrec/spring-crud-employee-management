package com.test.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {

    private String accessToken;
    public String refreshToken;
}
