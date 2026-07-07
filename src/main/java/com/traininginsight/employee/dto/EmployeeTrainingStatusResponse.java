package com.traininginsight.employee.dto;

import java.util.List;

// 교육 상태 응답 DTO
// 직원 정보와 직원의 교육 목록을 담는다.
public record EmployeeTrainingStatusResponse(
        List<EmployeeResult> results
) {

    public record EmployeeResult(
            Long employeeId,
            String name,
            String department,
            String position,
            String hireType,
            List<TargetCourseResult> targetCourses
    ) {
    }

    public record TargetCourseResult(
            Long courseId,
            String title,
            String category,
            boolean required,
            boolean completed,
            String status
    ) {
    }
}
