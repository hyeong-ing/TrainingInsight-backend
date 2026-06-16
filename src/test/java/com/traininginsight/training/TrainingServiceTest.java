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
    void calculatesIncompleteEmployeesForRequiredCourseTargetingAllEmployees() {
        Result result = findResultByCourseId(1L);

        assertThat(result.targetDepartment()).isEqualTo("ALL");
        assertThat(result.targetHireType()).isEqualTo("ALL");
        assertThat(result.incompleteCount()).isEqualTo(3);
        assertThat(result.completionRate()).isEqualTo(25);
        assertThat(employeeIds(result)).containsExactly(2L, 3L, 4L);
    }

    @Test
    void calculatesIncompleteEmployeesForRequiredCourseTargetingSpecificDepartment() {
        Result result = findResultByCourseId(3L);

        assertThat(result.targetDepartment()).isEqualTo("Engineering");
        assertThat(result.incompleteCount()).isEqualTo(1);
        assertThat(result.completionRate()).isEqualTo(50);
        assertThat(employeeIds(result)).containsExactly(4L);
    }

    @Test
    void calculatesIncompleteEmployeesForRequiredCourseTargetingNewHires() {
        Result result = findResultByCourseId(2L);

        assertThat(result.targetHireType()).isEqualTo("신입");
        assertThat(result.incompleteCount()).isEqualTo(1);
        assertThat(result.completionRate()).isEqualTo(50);
        assertThat(employeeIds(result)).containsExactly(4L);
    }

    @Test
    void treatsEmployeeAsCompleteWhenCompletedRecordExists() {
        jdbcTemplate.update("""
                insert into training_records (id, employee_id, course_id, status, completed_at)
                values (100, 4, 1, 'COMPLETED', '2024-04-01T09:00:00')
                """);

        Result result = findResultByCourseId(1L);

        assertThat(result.incompleteCount()).isEqualTo(2);
        assertThat(result.completionRate()).isEqualTo(50);
        assertThat(employeeIds(result)).containsExactly(2L, 3L);
    }

    @Test
    void completedRecordWinsWhenEmployeeHasDuplicateTrainingRecordsForSameCourse() {
        jdbcTemplate.update("""
                insert into training_records (id, employee_id, course_id, status, completed_at)
                values (101, 2, 1, 'COMPLETED', '2024-04-01T09:00:00')
                """);

        Result result = findResultByCourseId(1L);

        assertThat(result.incompleteCount()).isEqualTo(2);
        assertThat(result.completionRate()).isEqualTo(50);
        assertThat(employeeIds(result)).containsExactly(3L, 4L);
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
