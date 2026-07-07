package com.traininginsight.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.dashboard.dto.DashboardResponse;
import com.traininginsight.dashboard.dto.RequiredCourseStatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void countsAllEmployees() {
        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.employeeCount()).isEqualTo(24);
    }

    @Test
    void countsAllCourses() {
        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.courseCount()).isEqualTo(13);
    }

    @Test
    void countsRequiredCourses() {
        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.requiredCourseCount()).isEqualTo(5);
    }

    @Test
    void calculatesOverallCompletionRateFromAllTrainingRecords() {
        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.overallCompletionRate()).isEqualTo(36);
    }

    @Test
    void calculatesTotalIncompleteEmployeeCountUsingRequiredCourseRules() {
        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.totalIncompleteEmployeeCount()).isEqualTo(33);
        assertThat(statByCourseId(response, 1L).incompleteCount()).isEqualTo(8);
        assertThat(statByCourseId(response, 1L).completionRate()).isEqualTo(67);
        assertThat(statByCourseId(response, 2L).incompleteCount()).isEqualTo(12);
        assertThat(statByCourseId(response, 2L).completionRate()).isEqualTo(50);
        assertThat(statByCourseId(response, 3L).incompleteCount()).isEqualTo(6);
        assertThat(statByCourseId(response, 3L).completionRate()).isEqualTo(75);
        assertThat(statByCourseId(response, 4L).incompleteCount()).isEqualTo(5);
        assertThat(statByCourseId(response, 4L).completionRate()).isEqualTo(50);
        assertThat(statByCourseId(response, 5L).incompleteCount()).isEqualTo(2);
        assertThat(statByCourseId(response, 5L).completionRate()).isEqualTo(60);
    }

    @Test
    void returnsZeroValuesWithoutErrorWhenDataIsEmpty() {
        jdbcTemplate.update("delete from training_records");
        jdbcTemplate.update("delete from courses");
        jdbcTemplate.update("delete from employees");

        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.employeeCount()).isZero();
        assertThat(response.courseCount()).isZero();
        assertThat(response.requiredCourseCount()).isZero();
        assertThat(response.overallCompletionRate()).isZero();
        assertThat(response.totalIncompleteEmployeeCount()).isZero();
        assertThat(response.requiredCourseStats()).isEmpty();
    }

    private RequiredCourseStatResponse statByCourseId(DashboardResponse response, Long courseId) {
        return response.requiredCourseStats().stream()
                .filter(stat -> stat.courseId().equals(courseId))
                .findFirst()
                .orElseThrow();
    }
}
