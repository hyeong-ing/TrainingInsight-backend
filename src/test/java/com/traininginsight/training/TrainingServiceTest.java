package com.traininginsight.training;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.training.dto.IncompleteTrainingResponse;
import com.traininginsight.training.dto.IncompleteTrainingResponse.Result;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TrainingServiceTest {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void returnsAllCoursesIncludingOptionalCourses() {
        IncompleteTrainingResponse response = trainingService.getIncompleteTrainings();

        assertThat(response.results())
                .extracting(Result::courseId)
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L);
        assertThat(findResultByCourseId(7L).required()).isFalse();
    }

    @Test
    void calculatesIncompleteEmployeesForRequiredCourseTargetingAllEmployees() {
        Result result = findResultByCourseId(1L);

        assertThat(result.category()).isEqualTo("법정교육");
        assertThat(result.required()).isTrue();
        assertThat(result.targetDepartment()).isEqualTo("ALL");
        assertThat(result.targetHireType()).isEqualTo("ALL");
        assertThat(result.targetEmployeeCount()).isEqualTo(24);
        assertThat(result.incompleteCount()).isEqualTo(8);
        assertThat(result.completionRate()).isEqualTo(67);
        assertThat(employeeIds(result)).containsExactly(2L, 4L, 19L, 20L, 21L, 22L, 23L, 24L);
    }

    @Test
    void calculatesIncompleteEmployeesForRequiredCourseTargetingSpecificDepartment() {
        Result result = findResultByCourseId(5L);

        assertThat(result.targetDepartment()).isEqualTo("품질관리팀");
        assertThat(result.targetEmployeeCount()).isEqualTo(5);
        assertThat(result.incompleteCount()).isEqualTo(2);
        assertThat(result.completionRate()).isEqualTo(60);
        assertThat(employeeIds(result)).containsExactly(12L, 22L);
    }

    @Test
    void calculatesIncompleteEmployeesForRequiredCourseTargetingNewHires() {
        Result result = findResultByCourseId(4L);

        assertThat(result.targetHireType()).isEqualTo("신입");
        assertThat(result.targetEmployeeCount()).isEqualTo(10);
        assertThat(result.incompleteCount()).isEqualTo(5);
        assertThat(result.completionRate()).isEqualTo(50);
        assertThat(employeeIds(result)).containsExactly(14L, 16L, 18L, 22L, 24L);
    }

    @Test
    void includesInProgressNotStartedAndMissingRecordsAsIncomplete() {
        Result result = findResultByCourseId(7L);

        assertThat(result.required()).isFalse();
        assertThat(result.targetEmployeeCount()).isEqualTo(24);
        assertThat(result.incompleteCount()).isEqualTo(21);
        assertThat(result.completionRate()).isEqualTo(13);
        assertThat(employeeIds(result)).contains(8L, 10L, 18L, 2L);
        assertThat(employeeIds(result)).doesNotContain(1L, 4L, 13L);
    }

    @Test
    void treatsEmployeeAsCompleteWhenCompletedRecordExists() {
        jdbcTemplate.update("""
                insert into training_records (id, employee_id, course_id, status, completed_at)
                values (1000, 4, 1, 'COMPLETED', '2024-04-01T09:00:00')
                """);

        Result result = findResultByCourseId(1L);

        assertThat(result.incompleteCount()).isEqualTo(7);
        assertThat(result.completionRate()).isEqualTo(71);
        assertThat(employeeIds(result)).containsExactly(2L, 19L, 20L, 21L, 22L, 23L, 24L);
    }

    @Test
    void completedRecordWinsWhenEmployeeHasDuplicateTrainingRecordsForSameCourse() {
        jdbcTemplate.update("""
                insert into training_records (id, employee_id, course_id, status, completed_at)
                values (1001, 2, 1, 'COMPLETED', '2024-04-01T09:00:00')
                """);

        Result result = findResultByCourseId(1L);

        assertThat(result.incompleteCount()).isEqualTo(7);
        assertThat(result.completionRate()).isEqualTo(71);
        assertThat(employeeIds(result)).containsExactly(4L, 19L, 20L, 21L, 22L, 23L, 24L);
    }

    private Result findResultByCourseId(Long courseId) {
        IncompleteTrainingResponse response = trainingService.getIncompleteTrainings();

        return response.results().stream()
                .filter(result -> result.courseId().equals(courseId))
                .findFirst()
                .orElseThrow();
    }

    private List<Long> employeeIds(Result result) {
        return result.employees().stream()
                .map(employee -> employee.employeeId())
                .toList();
    }
}
