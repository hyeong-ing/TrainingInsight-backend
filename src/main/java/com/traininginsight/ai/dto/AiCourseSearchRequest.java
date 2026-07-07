package com.traininginsight.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiCourseSearchRequest(
        @NotBlank
        @Size(max = 500)
        String query
) {
}
