package com.test.employee.service;

import java.util.ArrayList;
import java.util.List;


import com.test.employee.dto.EmployeeRequest;
import com.test.employee.dto.EmployeeResponse;
import com.test.employee.dto.EmployeeUpdateRequest;
import com.test.employee.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.test.employee.entity.Employee;
import com.test.employee.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

//    public Employee postEmployee(Employee employee){
//        return employeeRepository.save(employee);
//    }

    // Create employee
    public EmployeeResponse postEmployee(EmployeeRequest employeeRequest) {

        Employee employee = new Employee();
        employee.setName(employeeRequest.getName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setPhone(employeeRequest.getPhone());

        Employee saved= employeeRepository.save(employee);

        return new EmployeeResponse(saved.getId(),saved.getName(),saved.getEmail(),saved.getPhone(),saved.getDepartment());

    }


//    public List<Employee> getAllEmployees(){
//        return employeeRepository.findAll();
//    }

    // Get all employees
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        List<EmployeeResponse> response = new ArrayList<>();

        for (Employee employee : employees) {
            response.add(new EmployeeResponse(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getPhone(),
                    employee.getDepartment()

            ));
        }
        return response;
    }

//    public void deleteEmployees(Long id){
//
//        if(!employeeRepository.existsById(id)){
//            throw new EntityNotFoundException("Employee with Id " +id +" Not found");
//        }
//
//        employeeRepository.deleteById(id);
//    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
//            throw new EntityNotFoundException("Employee with ID " + id + " not found");
            throw new ResourceNotFoundException("Employee " + id + " not found");
        }
        employeeRepository.deleteById(id);
    }


//    public Employee getEmployeeById(Long id){
//
//        return employeeRepository.findById(id).orElse(null);
//    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with ID " + id + " not found"));

        return new EmployeeResponse(
                employee.getId(), employee.getName(), employee.getEmail(), employee.getPhone(), employee.getDepartment()
        );
    }

//    public Employee updatedEmployee(Long id, Employee employee) {
//        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
//
//        if (optionalEmployee.isPresent()) {
//            Employee existingEmployee = optionalEmployee.get();
//            existingEmployee.setEmail(employee.getEmail());
//            existingEmployee.setName(employee.getName());
//            existingEmployee.setPhone(employee.getPhone());
//            existingEmployee.setDepartment(employee.getDepartment());
//
//
//            return employeeRepository.save(existingEmployee);
//        }
//        return null;
//    }


    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee " + id + " not found"));

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());

        Employee updated = employeeRepository.save(existing);

        return new EmployeeResponse(
                updated.getId(), updated.getName(), updated.getEmail(), updated.getPhone(), updated.getDepartment()
        );
    }


}
