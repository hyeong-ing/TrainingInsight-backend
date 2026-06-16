package com.traininginsight.training.dto;

import java.util.List;

public record IncompleteTrainingResponse(
        List<Result> results
) {

    public record Result(
            Long courseId,
            String courseTitle,
            String targetDepartment,
            String targetHireType,
            int incompleteCount,
            int completionRate,
            List<IncompleteEmployeeResponse> employees
    ) {
    }
}
