package com.test.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.employee.dto.EmployeeRequest;
import com.test.employee.dto.EmployeeResponse;
import com.test.employee.exception.ResourceNotFoundException;
import com.test.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateEmployeeSuccessfully() throws Exception {

        // eexpected responses from service
        EmployeeResponse response = new EmployeeResponse(1L, "Saditha", "saditha@gmail.com", "77 77 77 7777", "IT");

        // Mock service layer
        when(employeeService.postEmployee(org.mockito.ArgumentMatchers.any(EmployeeRequest.class)))
                .thenReturn(response);

        // Prepare request with all required fields, input that sending to the api
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Saditha");
        request.setEmail("saditha@gmail.com");
        request.setPhone("77 77 77 7777");
        request.setDepartment("IT");

        // check returning correct jsons
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Saditha"))
                .andExpect(jsonPath("$.email").value("saditha@gmail.com"))
                .andExpect(jsonPath("$.department").value("IT"));
    }

    @Test
    void shouldFailValidationWhenCreatingEmployee() throws Exception {

        EmployeeRequest request = new EmployeeRequest();
        request.setName("");
        request.setEmail("not an email");
        request.setPhone(""); //
        // miss a field

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
//        Checks that the error messages returned by GlobalExceptionHandler match the @NotBlank and @Email messages.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.phone").value("Phone is required"))
                .andExpect(jsonPath("$.department").value("Department is required"));
    }


    // Get all employees

    @Test
    void shouldReturnAllEmployees() throws Exception {

        //   mock service response
        EmployeeResponse employee1 = new EmployeeResponse(1L, "Saditha", "saditha@gmail.com", "77 77 77 7777", "IT");
        EmployeeResponse employee2 = new EmployeeResponse(2L, "Sad", "sad@gmail.com", "77 77 77 7676", "HR");

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(employee1, employee2));

        //  GET request
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk()) //  Check HTTP 200
                .andExpect(jsonPath("$.size()").value(2)) // Check number of employees
                .andExpect(jsonPath("$[0].name").value("Saditha")) //  Check first employee
                .andExpect(jsonPath("$[1].name").value("Sad")) //  Check second employee
                .andExpect(jsonPath("$[1].department").value("HR"));
    }


// Employyes return by Id

    @Test
    void shouldReturnEmployeeByIdSuccessfully() throws Exception {

        // Prepare mock service response
        EmployeeResponse response = new EmployeeResponse(1L, "Saditha", "saditha@gmail.com", "77 77 77 7777", "IT");

        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        //  GET request
        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isOk()) //  Check HTTP 200
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saditha"))
                .andExpect(jsonPath("$.email").value("saditha@gmail.com"))
                .andExpect(jsonPath("$.phone").value("77 77 77 7777"))
                .andExpect(jsonPath("$.department").value("IT"));
    }


    @Test
    void shouldReturnNotFoundWhenEmployeeDoesNotExist() throws Exception {

        // Mock service to throw exception
        when(employeeService.getEmployeeById(1L))
                .thenThrow(new com.test.employee.exception.ResourceNotFoundException("Employee 1 not found"));

        //  Perform GET request
        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(content().string("Employee 1 not found")); // error message
    }
    // Delete TESTS

    @Test
    void shouldDeleteEmployeeSuccessfully() throws Exception {

        //  Mock service behavior
        doNothing().when(employeeService).deleteEmployee(1L);

        //  Perform DELETE request
        mockMvc.perform(delete("/api/employee/1"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().string("Employee with ID 1 deleted successfully"));

        // Verify service was called
        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingEmployee() throws Exception {

        //  Mock service to throw exception
        doThrow(new com.test.employee.exception.ResourceNotFoundException("Employee 1 not found"))
                .when(employeeService).deleteEmployee(1L);

        //  Perform DELETE request
        mockMvc.perform(delete("/api/employee/1"))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(content().string("Employee 1 not found"));

        //  Verify service was called
        verify(employeeService, times(1)).deleteEmployee(1L);
    }


    // Update employee tsets

    @Test
    void shouldUpdateEmployeeSuccessfully() throws Exception {

        EmployeeRequest request = new EmployeeRequest();
        request.setDepartment("HR"); // only updating department


        //  existing employee response after update
        EmployeeResponse response = new EmployeeResponse(
                1L,
                "Saditha",                 // existing name
                "saditha@gmail.com",       // existing email
                "77 77 77 7777",               // existing phone
                "HR"                    // updated department
        );

        //  Mock service to return updated employee
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class)))
                .thenReturn(response);

        //  PATCH request
        mockMvc.perform(patch("/api/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saditha"))
                .andExpect(jsonPath("$.email").value("saditha@gmail.com"))
                .andExpect(jsonPath("$.phone").value("77 77 77 7777"))
                .andExpect(jsonPath("$.department").value("HR"));
    }

    // ❌ PATCH test fails with 400 because EmployeeRequest has @NotBlank on name, email, phone
// Sending only 'department' triggers validation error for the other fields


    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingEmployee() throws Exception {
        // Prepare request
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Sas");
        request.setEmail("sas@gmail.com");
        request.setPhone("77 77 77 7777");
        request.setDepartment("HR");

        // Mock service to throw exception
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee 1 not found"));

        // Perform PATCH request
        mockMvc.perform(patch("/api/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee 1 not found"));
    }

}