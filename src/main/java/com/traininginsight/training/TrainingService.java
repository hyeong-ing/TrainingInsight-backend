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

// 대시보드에서 필요한 값을 가져다 쓰고
// 미수료자 자동 추출에서 교육별 미수료 현황에서 사용한다.
// 전체 강의 미수료자 조회 / 필수 강의 미수료자 조회
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingService {

    private static final String ALL = "ALL";

    private final CourseRepository courseRepository; // 강의 목록
    private final EmployeeRepository employeeRepository; // 직원 목록
    private final TrainingRecordRepository trainingRecordRepository; // 교육 이수 기록

    // 주요 메서드
    // TrainingController
    // 전체 강의 미수료 현황
    public IncompleteTrainingResponse getIncompleteTrainings() {
        // 모든 강의를 찾아오도록 하고 오름차순으로 정렬
        return getIncompleteTrainings(courseRepository.findAllByOrderByIdAsc());
    }

    // 주요 메서드
    // 필수 강의 미수료 현황
    public IncompleteTrainingResponse getRequiredIncompleteTrainings() {
        // required가 true인 것만 가져오도록 하고 오름차순으로 정렬
        return getIncompleteTrainings(courseRepository.findByRequiredTrueOrderByIdAsc());
    }

    // 전체 강의든, 필수 강의든 List<Course>로 들어와서 이 메서드에서 똑같이 처리한다.
    private IncompleteTrainingResponse getIncompleteTrainings(List<Course> courses) {

        List<Employee> employees = employeeRepository.findAllByOrderByIdAsc(); // 전체 직원 조회
        Set<CompletionKey> completedKeys = getCompletedKeys(); // 완료된 교육 기록 조회 (Complete 상태만)

        // 강의별로 결과 만들기
        // 강의 목록을 하나씩 돌면서 각 강의별로 미수료 현황을 만든다.
        List<IncompleteTrainingResponse.Result> results = courses.stream()
                .map(course -> toIncompleteTrainingResult(course, employees, completedKeys))
                .toList();

        // 각각 계산해서 results에 담는다.
        return new IncompleteTrainingResponse(results);
    }

    // 강의 하나에 대한 미수료 현황을 계산한다
    private IncompleteTrainingResponse.Result toIncompleteTrainingResult(
            Course course,
            List<Employee> employees,
            Set<CompletionKey> completedKeys
    ) {
        // 들어온 교육명을 들어야 하는 직원을 찾아서 골라낸다.
        List<Employee> targetEmployees = employees.stream()
                // isTargetEmployee가 판단함
                .filter(employee -> isTargetEmployee(course, employee))
                .toList();

        // 이제 미수료 직원을 찾자.
        List<IncompleteEmployeeResponse> incompleteEmployees = targetEmployees.stream()
                // 해당 교육에 대한 COMPLETED 기록이 없는 직원을 찾는다.
                // CompletionKey에 각 id의 조합(1-3과 같은)이 포함되어있지 않으면
                .filter(employee -> !completedKeys.contains(new CompletionKey(course.getId(), employee.getId())))
                // 교육 미수료자로 판단된 직원 DTO를 바꾼다.
                .map(this::toIncompleteEmployeeResponse)
                .toList();

        // 강의별 수료율 계산
        int completionRate = calculateCompletionRate(targetEmployees.size(), incompleteEmployees.size());

        // 최종 결과 만들기 -> 강의 하나에 대한 결과를 만들어서 반환한다.
        return new IncompleteTrainingResponse.Result(
                course.getId(),
                course.getTitle(),
                course.getCategory(),
                course.isRequired(),
                course.getTargetDepartment(),
                course.getTargetHireType(),
                targetEmployees.size(),
                incompleteEmployees.size(),
                completionRate,
                incompleteEmployees
        );
    }

    private Set<CompletionKey> getCompletedKeys() {
        Set<CompletionKey> completedKeys = new HashSet<>();

        //COMPLETED 상태인 기록을 모두 가져와서 HashSet으로 저장한다. -> contains( )확인 빨리 하려고
        trainingRecordRepository.findAllByStatusWithEmployeeAndCourse(TrainingStatus.COMPLETED)
                .forEach(record -> completedKeys.add(new CompletionKey(
                        record.getCourse().getId(),
                        record.getEmployee().getId()
                )));
        return completedKeys;
    }

    // 해당 교육을 들어야하는 직원을 찾아내는 메서드
    private boolean isTargetEmployee(Course course, Employee employee) {
        // 교육의 대상 부서와 직원의 대상 부서가 맞는지 확인한다. 그리고 교육의 입사 유형과 직원의 입사 유형이 맞는지 확인한다.
        return matchesTarget(course.getTargetDepartment(), employee.getDepartment())
                && matchesTarget(course.getTargetHireType(), employee.getHireType());
    }

    // 교육 조건과 직원 정보를 비교하는 메서드
    private boolean matchesTarget(String target, String employeeValue) {
        // 교육 조건이 ALL이면 통과 혹은 교육 조건과 직원 값이 같으면 통과
        return ALL.equals(target) || target.equals(employeeValue);
    }

    // 교육별 이수율 계산
    private int calculateCompletionRate(int targetCount, int incompleteCount) {
        if (targetCount == 0) {
            return 0;
        }
        int completedCount = targetCount - incompleteCount; // 완료자 = 대상 직원 수 - 미수료자 수
        return (int) Math.round((completedCount * 100.0) / targetCount); // 이수율 = 완료자 수 / 대상 직원 수 x 100
    }

    // 교육 미수료 직원 엔티티 그대로 보내지 않고 아래 DTO로 반환해서 팰요한 정보만 보낸다.
    private IncompleteEmployeeResponse toIncompleteEmployeeResponse(Employee employee) {
        return new IncompleteEmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getHireType()
        );
    }

    // 미수료자를 찾기 위해 courseId + employeeId 조합으로 완료 여부를 확인하는 키.
    // A 직원이 강의 1을 완료했는가?를 본다.
    private record CompletionKey(Long courseId, Long employeeId) {
    }
}
