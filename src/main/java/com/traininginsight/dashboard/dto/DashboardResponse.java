package com.traininginsight.dashboard.dto;

import java.util.List;

// 대시보드 전체 응답 DTO
public record DashboardResponse(
        long employeeCount,
        long courseCount,
        long requiredCourseCount,
        int overallCompletionRate,
        int totalIncompleteEmployeeCount,
        List<RequiredCourseStatResponse> requiredCourseStats
) {
}
