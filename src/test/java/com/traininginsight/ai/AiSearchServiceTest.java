package com.traininginsight.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.ai.dto.AiCourseSearchResponse;
import com.traininginsight.course.dto.CourseSearchResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AiSearchServiceTest {

    @Autowired
    private AiSearchService aiSearchService;

    @Test
    void searchesRequiredCoursesForNewHires() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("신입사원이 들어야 하는 필수 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().required()).isTrue();
        assertThat(response.interpretedCondition().targetHireType()).isEqualTo("신입");
        assertThat(response.interpretedCondition().category()).isEqualTo("온보딩");
        assertThat(courseIds(response)).containsExactly(4L);
    }

    @Test
    void searchesFoodSafetyRequiredCoursesForQualityDepartmentWithoutError() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("품질관리팀 식품안전 필수교육");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("식품안전");
        assertThat(response.interpretedCondition().required()).isTrue();
        assertThat(response.interpretedCondition().targetDepartment()).isEqualTo("품질관리팀");
        assertThat(response.interpretedCondition().category()).isEqualTo("품질관리");
        assertThat(courseIds(response)).containsExactly(5L);
    }

    @Test
    void searchesAiAutomationCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("AI 업무 자동화 교육");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("AI");
        assertThat(response.interpretedCondition().category()).isEqualTo("AI");
        assertThat(courseIds(response)).containsExactly(7L);
    }

    @Test
    void usesOriginalQueryAsKeywordWhenQueryCannotBeInterpreted() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("알수없는검색어");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("알수없는검색어");
        assertThat(response.results()).isEmpty();
    }

    @Test
    void searchesDataAnalysisCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("데이터 분석 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().category()).isEqualTo("데이터");
        assertThat(courseIds(response)).containsExactly(8L);
    }

    @Test
    void searchesEsgCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("ESG 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().category()).isEqualTo("ESG");
        assertThat(courseIds(response)).containsExactly(11L);
    }

    @Test
    void searchesSalesCustomerResponseCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("영업팀 고객 응대 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().targetDepartment()).isEqualTo("영업팀");
        assertThat(response.interpretedCondition().category()).isEqualTo("영업");
        assertThat(courseIds(response)).containsExactly(12L);
    }

    @Test
    void searchesDevelopmentSecureCodingCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("개발팀 보안 코딩 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().targetDepartment()).isEqualTo("개발팀");
        assertThat(response.interpretedCondition().category()).isEqualTo("개발");
        assertThat(courseIds(response)).containsExactly(13L);
    }

    @Test
    void searchesRequiredCoursesForAllEmployees() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("전 직원 필수교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().required()).isTrue();
        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void searchesEthicsComplianceCourses() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("윤리경영 준법 교육 찾아줘");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("윤리");
        assertThat(response.interpretedCondition().category()).isEqualTo("준법교육");
        assertThat(courseIds(response)).containsExactly(2L);
    }

    @Test
    void returnsEmptyResultsWhenNoCourseMatches() {
        AiCourseSearchResponse response = aiSearchService.searchCourses("없는교육");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("없는교육");
        assertThat(response.results()).isEmpty();
    }

    private List<Long> courseIds(AiCourseSearchResponse response) {
        return response.results().stream()
                .map(CourseSearchResponse.Result::courseId)
                .toList();
    }
}
