package com.traininginsight.course.dto;

import java.util.List;

// 교육 검색 결과 DTO
public record CourseSearchResponse(
        List<Result> results
) {

    public record Result(
            Long courseId,
            String title,
            String category,
            String description,
            boolean required,
            String targetDepartment,
            String targetHireType
    ) {
    }
}
