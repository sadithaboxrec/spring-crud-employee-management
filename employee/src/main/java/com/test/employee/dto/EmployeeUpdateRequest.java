package com.test.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeUpdateRequest {

    private String name;
    private String email;
    private String phone;
    private String department;
}