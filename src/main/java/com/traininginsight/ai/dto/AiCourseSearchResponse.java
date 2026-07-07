package com.traininginsight.ai.dto;

import com.traininginsight.course.dto.CourseSearchResponse;
import java.util.List;

public record AiCourseSearchResponse(
        AiSearchCondition interpretedCondition,
        String reason,
        boolean fallbackUsed,
        List<CourseSearchResponse.Result> results
) {
}
