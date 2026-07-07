package com.traininginsight.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.employee.dto.EmployeeTrainingStatusResponse;
import com.traininginsight.employee.dto.EmployeeTrainingStatusResponse.EmployeeResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmployeeTrainingServiceTest {

    @Autowired
    private EmployeeTrainingService employeeTrainingService;

    @Test
    void returnsTargetCoursesForEmployees() {
        EmployeeResult employee = findEmployee(1L);

        assertThat(courseIds(employee)).contains(1L, 2L, 3L, 7L, 8L, 9L, 10L, 11L);
    }

    @Test
    void allDepartmentCoursesAreIncludedForEveryEmployee() {
        EmployeeResult employee = findEmployee(13L);

        assertThat(courseIds(employee)).contains(1L, 2L, 3L, 7L, 8L, 10L, 11L);
    }

    @Test
    void departmentSpecificCoursesAreIncludedOnlyForMatchingDepartment() {
        EmployeeResult qualityEmployee = findEmployee(2L);
        EmployeeResult salesEmployee = findEmployee(3L);

        assertThat(courseIds(qualityEmployee)).contains(5L, 6L);
        assertThat(courseIds(salesEmployee)).doesNotContain(5L, 6L);
    }

    @Test
    void newHireCoursesAreIncludedOnlyForNewHires() {
        EmployeeResult newHire = findEmployee(3L);
        EmployeeResult experienced = findEmployee(1L);

        assertThat(courseIds(newHire)).contains(4L);
        assertThat(courseIds(experienced)).doesNotContain(4L);
    }

    @Test
    void completedRecordMarksCourseAsCompleted() {
        EmployeeResult employee = findEmployee(1L);

        EmployeeTrainingStatusResponse.TargetCourseResult course = findCourse(employee, 1L);

        assertThat(course.completed()).isTrue();
        assertThat(course.status()).isEqualTo("COMPLETED");
    }

    @Test
    void missingRecordMarksCourseAsNotStarted() {
        EmployeeResult employee = findEmployee(1L);

        EmployeeTrainingStatusResponse.TargetCourseResult course = findCourse(employee, 10L);

        assertThat(course.completed()).isFalse();
        assertThat(course.status()).isEqualTo("NOT_STARTED");
    }

    private EmployeeResult findEmployee(Long employeeId) {
        return employeeTrainingService.getEmployeeTrainingStatuses().results().stream()
                .filter(employee -> employee.employeeId().equals(employeeId))
                .findFirst()
                .orElseThrow();
    }

    private EmployeeTrainingStatusResponse.TargetCourseResult findCourse(EmployeeResult employee, Long courseId) {
        return employee.targetCourses().stream()
                .filter(course -> course.courseId().equals(courseId))
                .findFirst()
                .orElseThrow();
    }

    private List<Long> courseIds(EmployeeResult employee) {
        return employee.targetCourses().stream()
                .map(EmployeeTrainingStatusResponse.TargetCourseResult::courseId)
                .toList();
    }
}
