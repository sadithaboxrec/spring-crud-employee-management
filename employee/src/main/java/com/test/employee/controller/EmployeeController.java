package com.test.employee.controller;

import java.util.List;


import com.test.employee.dto.EmployeeRequest;
import com.test.employee.dto.EmployeeResponse;
import com.test.employee.dto.EmployeeUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.employee.entity.Employee;
import com.test.employee.service.EmployeeService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")

public class EmployeeController {

    private final EmployeeService employeeService;

//    @PostMapping("/employee")
//    public Employee postEmployee(@RequestBody Employee employee) {
//        return employeeService.postEmployee(employee);
//    }

    @PostMapping("/employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> postEmployee(
           @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse response=employeeService.postEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
//
//    @GetMapping("/employees")
//    public  List<Employee> getAllEmployees(){
//        return employeeService.getAllEmployees();
//    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

//    @DeleteMapping("/employee/{id}")
//
//    public ResponseEntity<?> deleteEmployee(@PathVariable Long id){
//        try{
//            employeeService.deleteEmployees(id);
//            return new ResponseEntity<>("Employee with ID"+id+"deleted successfully",HttpStatus.OK);
//        }catch(EntityNotFoundException e){
//            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
//        }
//    }


    @DeleteMapping("/employee/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee with ID " + id + " deleted successfully");
    }


//    @GetMapping("/employee/{id}")
//    public ResponseEntity<?> getEmployeeById(@PathVariable Long id){
//
//        Employee employee = employeeService.getEmployeeById(id);
//
//        if(employee == null) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(employee);
//    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {

        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

//    @PatchMapping("/employee/{id}")
//public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
//    Employee updatedEmployee = employeeService.updatedEmployee(id, employee);
//
//    if (updatedEmployee == null) {
//        // Return a BAD_REQUEST response if employee not found
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                             .body("Employee with ID " + id + " not found.");
//    }
//
//    // Return OK response with updated employee
//    return ResponseEntity.ok(updatedEmployee);
//}

//    @PatchMapping("/employee/{id}")
//    public ResponseEntity<EmployeeResponse> updateEmployee(
//            @PathVariable Long id,
//            @Valid @RequestBody EmployeeRequest request) {
//
//        EmployeeResponse response = employeeService.updateEmployee(id, request);
//        return ResponseEntity.ok(response);
//    }

    @PatchMapping("/employee/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateRequest request) {

        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }






}
