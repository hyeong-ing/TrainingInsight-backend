package com.traininginsight.training;

import com.traininginsight.course.Course;
import com.traininginsight.course.CourseRepository;
import com.traininginsight.employee.Employee;
import com.traininginsight.employee.EmployeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// 프론트에서 체크박스 수료 상태를 바꿀 때 사용
@Service
@RequiredArgsConstructor
public class TrainingRecordService {

    private static final String ALL = "ALL";

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final TrainingRecordRepository trainingRecordRepository;

    // 체크박스 업데이트
    @Transactional
    public void updateCompletion(Long employeeId, Long courseId, boolean completed) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!isTargetCourse(employee, course)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee is not a target for this course");
        }

        TrainingStatus status = completed ? TrainingStatus.COMPLETED : TrainingStatus.NOT_STARTED;
        List<TrainingRecord> records = trainingRecordRepository.findAllByEmployeeIdAndCourseId(employeeId, courseId);

        if (records.isEmpty()) {
            trainingRecordRepository.save(new TrainingRecord(employee, course, status));
            return;
        }

        records.forEach(record -> record.updateStatus(status));
    }

    private boolean isTargetCourse(Employee employee, Course course) {
        return matchesTarget(course.getTargetDepartment(), employee.getDepartment())
                && matchesTarget(course.getTargetHireType(), employee.getHireType());
    }

    private boolean matchesTarget(String target, String employeeValue) {
        return ALL.equals(target) || target.equals(employeeValue);
    }
}
