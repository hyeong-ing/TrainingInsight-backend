package com.traininginsight.employee;

import com.traininginsight.course.Course;
import com.traininginsight.course.CourseRepository;
import com.traininginsight.employee.dto.EmployeeTrainingStatusResponse;
import com.traininginsight.training.TrainingRecord;
import com.traininginsight.training.TrainingRecordRepository;
import com.traininginsight.training.TrainingStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 직원별로 어떤 교육을 듣고 현재 이수 상태가 무엇인지 계산
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeTrainingService {

    private static final String ALL = "ALL";

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final TrainingRecordRepository trainingRecordRepository;

    public EmployeeTrainingStatusResponse getEmployeeTrainingStatuses() {
        // 직원정보랑 강의정보를 가져옴
        List<Employee> employees = employeeRepository.findAllByOrderByIdAsc();
        List<Course> courses = courseRepository.findAllByOrderByIdAsc();

        //
        Map<TrainingKey, List<TrainingRecord>> recordsByKey = trainingRecordRepository.findAllWithEmployeeAndCourse()
                .stream()
                .collect(Collectors.groupingBy(record -> new TrainingKey(
                        record.getEmployee().getId(),
                        record.getCourse().getId()
                )));

        List<EmployeeTrainingStatusResponse.EmployeeResult> results = employees.stream()
                .map(employee -> toEmployeeResult(employee, courses, recordsByKey))
                .toList();

        return new EmployeeTrainingStatusResponse(results);
    }

    private EmployeeTrainingStatusResponse.EmployeeResult toEmployeeResult(
            Employee employee,
            List<Course> courses,
            Map<TrainingKey, List<TrainingRecord>> recordsByKey
    ) {
        List<EmployeeTrainingStatusResponse.TargetCourseResult> targetCourses = courses.stream()
                .filter(course -> isTargetCourse(employee, course))
                .map(course -> toTargetCourseResult(course, recordsByKey.get(new TrainingKey(employee.getId(), course.getId()))))
                .sorted(Comparator.comparing(EmployeeTrainingStatusResponse.TargetCourseResult::courseId))
                .toList();

        return new EmployeeTrainingStatusResponse.EmployeeResult(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getHireType(),
                targetCourses
        );
    }

    private EmployeeTrainingStatusResponse.TargetCourseResult toTargetCourseResult(
            Course course,
            List<TrainingRecord> records
    ) {
        TrainingStatus status = resolveStatus(records);

        return new EmployeeTrainingStatusResponse.TargetCourseResult(
                course.getId(),
                course.getTitle(),
                course.getCategory(),
                course.isRequired(),
                status == TrainingStatus.COMPLETED,
                status.name()
        );
    }

    private TrainingStatus resolveStatus(List<TrainingRecord> records) {
        if (records == null || records.isEmpty()) {
            return TrainingStatus.NOT_STARTED;
        }
        if (records.stream().anyMatch(record -> record.getStatus() == TrainingStatus.COMPLETED)) {
            return TrainingStatus.COMPLETED;
        }
        if (records.stream().anyMatch(record -> record.getStatus() == TrainingStatus.IN_PROGRESS)) {
            return TrainingStatus.IN_PROGRESS;
        }
        return TrainingStatus.NOT_STARTED;
    }

    private boolean isTargetCourse(Employee employee, Course course) {
        return matchesTarget(course.getTargetDepartment(), employee.getDepartment())
                && matchesTarget(course.getTargetHireType(), employee.getHireType());
    }

    private boolean matchesTarget(String target, String employeeValue) {
        return ALL.equals(target) || target.equals(employeeValue);
    }

    private record TrainingKey(Long employeeId, Long courseId) {
    }
}
