package com.traininginsight.course;

import com.traininginsight.course.dto.CourseSearchResponse;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 교육 검색 비즈니스 로직
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private static final String ALL = "ALL";

    private final CourseRepository courseRepository;

    // 검색 조건
    public CourseSearchResponse searchCourses(
            String keyword,
            String category,
            Boolean required,
            String department,
            String hireType
    ) {
        SearchCondition condition = new SearchCondition(
                normalize(keyword),
                normalize(category),
                required,
                normalize(department),
                normalize(hireType)
        );

        List<CourseSearchResponse.Result> results = courseRepository.findAllByOrderByIdAsc().stream()
                .filter(course -> matchesKeyword(course, condition.keyword()))
                .filter(course -> matchesExact(course.getCategory(), condition.category()))
                .filter(course -> condition.required() == null || course.isRequired() == condition.required())
                .filter(course -> matchesTarget(course.getTargetDepartment(), condition.department()))
                .filter(course -> matchesTarget(course.getTargetHireType(), condition.hireType()))
                .map(this::toResult)
                .toList();

        return new CourseSearchResponse(results);
    }

    private boolean matchesKeyword(Course course, String keyword) {
        if (keyword == null) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return course.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                || course.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword);
    }

    private boolean matchesExact(String courseValue, String conditionValue) {
        return conditionValue == null || courseValue.equalsIgnoreCase(conditionValue);
    }

    private boolean matchesTarget(String target, String conditionValue) {
        return conditionValue == null || ALL.equals(target) || target.equalsIgnoreCase(conditionValue);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private CourseSearchResponse.Result toResult(Course course) {
        return new CourseSearchResponse.Result(
                course.getId(),
                course.getTitle(),
                course.getCategory(),
                course.getDescription(),
                course.isRequired(),
                course.getTargetDepartment(),
                course.getTargetHireType()
        );
    }

    private record SearchCondition(
            String keyword,
            String category,
            Boolean required,
            String department,
            String hireType
    ) {
    }
}
