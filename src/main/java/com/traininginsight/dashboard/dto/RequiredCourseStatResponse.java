package com.traininginsight.dashboard.dto;

// 필수교육 하나의 통계 DTO
public record RequiredCourseStatResponse(
        Long courseId,
        String courseTitle,
        int incompleteCount,
        int completionRate
) {
}
