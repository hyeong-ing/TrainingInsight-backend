package com.traininginsight.ai;

import com.traininginsight.ai.dto.AiCourseSearchResponse;
import com.traininginsight.ai.dto.AiSearchCondition;
import com.traininginsight.course.Course;
import com.traininginsight.course.CourseRepository;
import com.traininginsight.course.CourseService;
import com.traininginsight.course.dto.CourseSearchResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// AI 검색
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiSearchService {

    private static final String ALL = "ALL";

    private final AiClient aiClient;
    private final CourseService courseService;
    private final CourseRepository courseRepository;

    // 사용자 자연어 query 받기
    public AiCourseSearchResponse searchCourses(String query) {
        try {
            AiSearchCondition condition = sanitize(aiClient.interpretCourseSearchCondition(query));
            CourseSearchResponse courseSearchResponse = courseService.searchCourses(
                    condition.keyword(),
                    condition.category(),
                    condition.required(),
                    condition.targetDepartment(),
                    condition.targetHireType()
            );

            return new AiCourseSearchResponse(
                    condition,
                    buildReason(condition),
                    false,
                    courseSearchResponse.results()
            );
        } catch (RuntimeException exception) {
            AiSearchCondition fallbackCondition = new AiSearchCondition(query, null, null, null, null);
            CourseSearchResponse courseSearchResponse = courseService.searchCourses(query, null, null, null, null);

            return new AiCourseSearchResponse(
                    fallbackCondition,
                    "AI 검색 조건 해석에 실패해 원문 키워드로 검색했습니다.",
                    true,
                    courseSearchResponse.results()
            );
        }
    }

    private AiSearchCondition sanitize(AiSearchCondition condition) {
        if (condition == null) {
            return new AiSearchCondition(null, null, null, null, null);
        }

        Set<String> categories = courseRepository.findAllByOrderByIdAsc().stream()
                .map(Course::getCategory)
                .collect(Collectors.toSet());
        Set<String> departments = courseRepository.findAllByOrderByIdAsc().stream()
                .map(Course::getTargetDepartment)
                .filter(value -> !ALL.equals(value))
                .collect(Collectors.toSet());
        Set<String> hireTypes = courseRepository.findAllByOrderByIdAsc().stream()
                .map(Course::getTargetHireType)
                .filter(value -> !ALL.equals(value))
                .collect(Collectors.toSet());

        return new AiSearchCondition(
                normalize(condition.keyword()),
                keepIfKnown(normalize(condition.category()), categories),
                condition.required(),
                keepTargetIfKnown(normalize(condition.targetDepartment()), departments),
                keepTargetIfKnown(normalize(condition.targetHireType()), hireTypes)
        );
    }

    private String keepIfKnown(String value, Set<String> knownValues) {
        if (value == null) {
            return null;
        }
        return knownValues.stream()
                .filter(knownValue -> knownValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    private String keepTargetIfKnown(String value, Set<String> knownValues) {
        if (value == null || ALL.equals(value)) {
            return value;
        }
        return keepIfKnown(value, knownValues);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String buildReason(AiSearchCondition condition) {
        if (condition.required() != null || condition.targetDepartment() != null || condition.targetHireType() != null
                || condition.category() != null) {
            return "검색어에서 교육 검색 조건을 해석했습니다.";
        }
        if (condition.keyword() != null) {
            return "명확한 구조화 조건이 없어 원문을 키워드로 사용했습니다.";
        }
        return "검색 조건을 찾지 못해 전체 교육 과정을 조회했습니다.";
    }
}
