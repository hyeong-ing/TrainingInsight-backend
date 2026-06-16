package com.traininginsight.training;

import com.traininginsight.course.Course;
import com.traininginsight.course.CourseRepository;
import com.traininginsight.employee.Employee;
import com.traininginsight.employee.EmployeeRepository;
import com.traininginsight.training.dto.IncompleteEmployeeResponse;
import com.traininginsight.training.dto.IncompleteTrainingResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingService {

    private static final String ALL = "ALL";

    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainingRecordRepository trainingRecordRepository;

    public IncompleteTrainingResponse getIncompleteTrainings() {
        List<Course> requiredCourses = courseRepository.findByRequiredTrueOrderByIdAsc();
        List<Employee> employees = employeeRepository.findAllByOrderByIdAsc();
        Set<CompletionKey> completedKeys = getCompletedKeys();

        List<IncompleteTrainingResponse.Result> results = requiredCourses.stream()
                .map(course -> toIncompleteTrainingResult(course, employees, completedKeys))
                .toList();

        return new IncompleteTrainingResponse(results);
    }

    private IncompleteTrainingResponse.Result toIncompleteTrainingResult(
            Course course,
            List<Employee> employees,
            Set<CompletionKey> completedKeys
    ) {
        List<Employee> targetEmployees = employees.stream()
                .filter(employee -> isTargetEmployee(course, employee))
                .toList();

        List<IncompleteEmployeeResponse> incompleteEmployees = targetEmployees.stream()
                .filter(employee -> !completedKeys.contains(new CompletionKey(course.getId(), employee.getId())))
                .map(this::toIncompleteEmployeeResponse)
                .toList();

        int completionRate = calculateCompletionRate(targetEmployees.size(), incompleteEmployees.size());

        return new IncompleteTrainingResponse.Result(
                course.getId(),
                course.getTitle(),
                course.getTargetDepartment(),
                course.getTargetHireType(),
                incompleteEmployees.size(),
                completionRate,
                incompleteEmployees
        );
    }

    private Set<CompletionKey> getCompletedKeys() {
        Set<CompletionKey> completedKeys = new HashSet<>();
        trainingRecordRepository.findAllByStatusWithEmployeeAndCourse(TrainingStatus.COMPLETED)
                .forEach(record -> completedKeys.add(new CompletionKey(
                        record.getCourse().getId(),
                        record.getEmployee().getId()
                )));
        return completedKeys;
    }

    private boolean isTargetEmployee(Course course, Employee employee) {
        return matchesTarget(course.getTargetDepartment(), employee.getDepartment())
                && matchesTarget(course.getTargetHireType(), employee.getHireType());
    }

    private boolean matchesTarget(String target, String employeeValue) {
        return ALL.equals(target) || target.equals(employeeValue);
    }

    private int calculateCompletionRate(int targetCount, int incompleteCount) {
        if (targetCount == 0) {
            return 0;
        }
        int completedCount = targetCount - incompleteCount;
        return (int) Math.round((completedCount * 100.0) / targetCount);
    }

    private IncompleteEmployeeResponse toIncompleteEmployeeResponse(Employee employee) {
        return new IncompleteEmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getHireType()
        );
    }

    private record CompletionKey(Long courseId, Long employeeId) {
    }
}
