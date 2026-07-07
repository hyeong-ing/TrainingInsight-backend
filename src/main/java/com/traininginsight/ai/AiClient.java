package com.traininginsight.ai;

import com.traininginsight.ai.dto.AiSearchCondition;

// AI 검색 조건 해석을 추상화한 인터페이스
public interface AiClient {

    AiSearchCondition interpretCourseSearchCondition(String query);
}
