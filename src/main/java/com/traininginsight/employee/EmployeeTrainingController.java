package com.traininginsight.employee;

import com.traininginsight.employee.dto.EmployeeTrainingStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 직원별 교육 이수 상태 조회 API 함수 fetchEmployeeTrainingStatuses()
// IncompletePage.jsx에서 옴, GET /api/employees/training-status
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeTrainingController {

    private final EmployeeTrainingService employeeTrainingService;

    @GetMapping("/training-status")
    public EmployeeTrainingStatusResponse getTrainingStatuses() {
        return employeeTrainingService.getEmployeeTrainingStatuses();
    }
}
