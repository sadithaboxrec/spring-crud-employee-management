package com.test.employee.service;

import com.test.employee.dto.EmployeeRequest;
import com.test.employee.dto.EmployeeResponse;
import com.test.employee.entity.Employee;
import com.test.employee.exception.ResourceNotFoundException;
import com.test.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;


    //  Create Employee
    @Test
    void shouldCreateEmployeeSuccessfully() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Saditha");
        request.setEmail("saditha@gmail.com");
        request.setPhone("77 77 77 7777");
        request.setDepartment("IT");

        Employee saved = new Employee();
        saved.setId(1L);
        saved.setName("Saditha");
        saved.setEmail("saditha@gmail.com");
        saved.setPhone("77 77 77 7777");
        saved.setDepartment("IT");

        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse response = employeeService.postEmployee(request);

        assertNotNull(response);
        assertEquals("Saditha", response.getName());
        assertEquals("IT", response.getDepartment());

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }


    @Test
    void shouldReturnAllEmployees() {
        // Prepare mock employee list
        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setName("Saditha");

        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setName("Sad");

        // Mock repository to return list
        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        // Call service method
        List<EmployeeResponse> result = employeeService.getAllEmployees();


        assertEquals(2, result.size());           //  number of employees
        assertEquals("Saditha", result.get(0).getName());  // first employee
        assertEquals("Sad", result.get(1).getName());      // second employee
    }



    // Get EmployeeById

    @Test
    void shouldReturnEmployeeById() {
        // Prepare mock employee
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setName("Saditha");
        emp.setEmail("saditha@gmail.com");
        emp.setPhone("77 77 77 7777");
        emp.setDepartment("IT");

        // Mock repository
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        // Call service
        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertEquals("Saditha", response.getName());
        assertEquals("IT", response.getDepartment());
        assertEquals("saditha@gmail.com", response.getEmail());
    }



    @Test
    void shouldThrowWhenEmployeeNotFound() {
        // Mock repository to return empty
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Call service and assert exception
        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(1L));
    }


//    Service getEmployeeById() currently throws EntityNotFoundException when employee is not found.
//    Not the  ResourceNotFoundException.
//           Need to  Change orElseThrow to throw ResourceNotFoundException .


    // ==  Delete RECORDS ====

    @Test
    void shouldDeleteEmployeeSuccessfully() {
        // Mock repo to say employee exists
        when(employeeRepository.existsById(1L)).thenReturn(true);

        // Call service
        employeeService.deleteEmployee(1L);

        // Verify repository delete is called once
        verify(employeeRepository, times(1)).deleteById(1L);
    }


    @Test
    void shouldThrowWhenDeletingNonExistingEmployee() {
        // Mock repository to say employee does NOT exist
        when(employeeRepository.existsById(1L)).thenReturn(false);

        // Call service and assert exception
        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(1L));
    }



          //     Update Records

    @Test
    void shouldUpdateEmployeeSuccessfully() {
        // Only department  updated
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartment("HR"); // other fields are null

        // Existing employee in repository
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setName("Saditha");
        existing.setEmail("saditha@gmail.com");
        existing.setPhone("77 77 77 7777");
        existing.setDepartment("IT");

        // Mock repository
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call service
        EmployeeResponse response = employeeService.updateEmployee(1L, request);


        assertEquals("HR", response.getDepartment()); // Updated field
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhone());

    }

    //  Current EmployeeRequest DTO requires all fields non-blank.
   // As a result, partial PATCH updates  fail validation
   // at the controller layer. Service does not update fields if they are null.


    @Test
    void shouldThrowWhenUpdatingNonExistingEmployee() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Sas");
        request.setEmail("sas@gmail.com");
        request.setPhone("77 77 77 7777");
        request.setDepartment("HR");

        // Mock repository to throw exception
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(1L, request));
    }
}