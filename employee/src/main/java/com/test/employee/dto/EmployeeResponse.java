package com.test.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String department;
}