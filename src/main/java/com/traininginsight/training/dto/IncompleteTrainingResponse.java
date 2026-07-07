package com.traininginsight.training.dto;

import java.util.List;

// 교육별 미수료 현황 응답 DTO
public record IncompleteTrainingResponse(
        List<Result> results
) {

    // 강의 관련된 정보, 부서, 직무, 들어야하는 직원 수, 미수료자 수, 수료 비율, 직원 정보
    public record Result(
            Long courseId,
            String courseTitle,
            String category,
            boolean required,
            String targetDepartment,
            String targetHireType,
            int targetEmployeeCount,
            int incompleteCount,
            int completionRate,
            List<IncompleteEmployeeResponse> employees
    ) {
    }
}
