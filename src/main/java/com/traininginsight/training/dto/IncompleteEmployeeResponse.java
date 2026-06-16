package com.traininginsight.training.dto;

public record IncompleteEmployeeResponse(
        Long employeeId,
        String name,
        String department,
        String position,
        String hireType
) {
}
