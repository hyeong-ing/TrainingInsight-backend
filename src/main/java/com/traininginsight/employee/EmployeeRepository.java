package com.traininginsight.employee;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 직원 리포지토리
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByIdAsc();
}
