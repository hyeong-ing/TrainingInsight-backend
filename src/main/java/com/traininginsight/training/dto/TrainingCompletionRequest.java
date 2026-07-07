package com.traininginsight.training.dto;

import jakarta.validation.constraints.NotNull;

// 수료 상태 변경 요청 DTO
public record TrainingCompletionRequest(
        @NotNull
        Long employeeId,
        @NotNull
        Long courseId,
        boolean completed
) {
}
