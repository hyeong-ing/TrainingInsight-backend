package com.traininginsight.training.dto;

// 미수료 직원 한 명의 정보를 담는 응답 응답 DTO
public record IncompleteEmployeeResponse(
        // 직원id, 이름, 부서, 직무
        Long employeeId,
        String name,
        String department,
        String position,
        String hireType
) {
}
