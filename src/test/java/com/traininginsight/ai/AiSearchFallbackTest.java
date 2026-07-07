package com.traininginsight.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.traininginsight.ai.dto.AiCourseSearchResponse;
import com.traininginsight.course.dto.CourseSearchResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AiSearchFallbackTest {

    @Autowired
    private AiSearchService aiSearchService;

    @MockBean
    private AiClient aiClient;

    @Test
    void fallsBackToKeywordSearchWhenAiClientFails() {
        when(aiClient.interpretCourseSearchCondition("식품안전"))
                .thenThrow(new IllegalStateException("AI unavailable"));

        AiCourseSearchResponse response = aiSearchService.searchCourses("식품안전");

        assertThat(response.fallbackUsed()).isTrue();
        assertThat(response.interpretedCondition().keyword()).isEqualTo("식품안전");
        assertThat(courseIds(response)).containsExactly(5L);
    }

    @Test
    void handlesNullConditionFromAiClientWithoutError() {
        when(aiClient.interpretCourseSearchCondition("전체 교육"))
                .thenReturn(null);

        AiCourseSearchResponse response = aiSearchService.searchCourses("전체 교육");

        assertThat(response.fallbackUsed()).isFalse();
        assertThat(response.interpretedCondition().keyword()).isNull();
        assertThat(courseIds(response)).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L);
    }

    private List<Long> courseIds(AiCourseSearchResponse response) {
        return response.results().stream()
                .map(CourseSearchResponse.Result::courseId)
                .toList();
    }
}
