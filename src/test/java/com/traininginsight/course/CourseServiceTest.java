package com.traininginsight.course;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.course.dto.CourseSearchResponse;
import com.traininginsight.course.dto.CourseSearchResponse.Result;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    void searchesCoursesByKeyword() {
        CourseSearchResponse response = courseService.searchCourses("식품안전", null, null, null, null);

        assertThat(courseIds(response)).containsExactly(5L);
    }

    @Test
    void searchesCoursesByCategoryIgnoringCase() {
        CourseSearchResponse response = courseService.searchCourses(null, "AI", null, null, null);

        assertThat(courseIds(response)).containsExactly(7L);
    }

    @Test
    void filtersRequiredCourses() {
        CourseSearchResponse response = courseService.searchCourses(null, null, true, null, null);

        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void departmentConditionReturnsCoursesTargetingDepartmentOrAll() {
        CourseSearchResponse response = courseService.searchCourses(null, null, null, "품질관리팀", null);

        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 11L);
    }

    @Test
    void hireTypeConditionReturnsCoursesTargetingHireTypeOrAll() {
        CourseSearchResponse response = courseService.searchCourses(null, null, null, null, "신입");

        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 11L, 12L, 13L);
    }

    @Test
    void searchesCoursesWithMultipleConditions() {
        CourseSearchResponse response = courseService.searchCourses("개인정보", "법정교육", true, "인사팀", "경력");

        assertThat(courseIds(response)).containsExactly(1L);
    }

    @Test
    void returnsAllCoursesWhenNoConditionExists() {
        CourseSearchResponse response = courseService.searchCourses(null, " ", null, "", null);

        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L);
    }

    @Test
    void returnsEmptyResultsWhenNoCourseMatches() {
        CourseSearchResponse response = courseService.searchCourses("없는교육", null, null, null, null);

        assertThat(response.results()).isEmpty();
    }

    private List<Long> courseIds(CourseSearchResponse response) {
        return response.results().stream()
                .map(Result::courseId)
                .toList();
    }
}
