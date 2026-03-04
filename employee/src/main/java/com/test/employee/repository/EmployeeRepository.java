package com.test.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository <Employee,Long>{

}
