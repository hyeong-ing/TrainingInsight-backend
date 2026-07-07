package com.traininginsight.ai.dto;

// AI가 자연어에서 뽑아낸 구조화 검색 조건 DTO
public record AiSearchCondition(
        String keyword,
        String category,
        Boolean required,
        String targetDepartment,
        String targetHireType
) {
}
