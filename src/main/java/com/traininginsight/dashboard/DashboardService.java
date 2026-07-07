package com.traininginsight.dashboard;

import com.traininginsight.course.CourseRepository;
import com.traininginsight.dashboard.dto.DashboardResponse;
import com.traininginsight.dashboard.dto.RequiredCourseStatResponse;
import com.traininginsight.employee.EmployeeRepository;
import com.traininginsight.training.TrainingService;
import com.traininginsight.training.dto.IncompleteTrainingResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 대시보드 통계 계산 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회만 하는 읽기 전용 트랜잭션
public class DashboardService {

    private final EmployeeRepository employeeRepository; // 전체 직원 수
    private final CourseRepository courseRepository; //전체 강의 수 | 필수 강의 수
    private final TrainingService trainingService; // 필수 교육 미수료 현황

    public DashboardResponse getDashboard() {

        long employeeCount = employeeRepository.count(); // 전체 직원 수
        long courseCount = courseRepository.count(); // 전체 강의 수
        long requiredCourseCount = courseRepository.countByRequiredTrue(); // 필수 강의 수
        int overallCompletionRate = calculateOverallCompletionRate(trainingService.getIncompleteTrainings()); // 전체 교육 이수율

        // 필수 교욱 미수료 현황
        IncompleteTrainingResponse incompleteTrainings = trainingService.getRequiredIncompleteTrainings();
        // 전체 미수료자 수 계산
        // incompleteTrainings.results() 안에 강의별 미수료 현황이 들어 있다. { courseTitle: "개인정보보호 교육", incompleteCount: 3 },
        // 그러면 incompleteCount만 뽑아서 모두 더한다.
        int totalIncompleteEmployeeCount = incompleteTrainings.results().stream() // results를 stream으로 바꾸기
                .mapToInt(IncompleteTrainingResponse.Result::incompleteCount) // result에서 incompleteCount 값만 꺼냄
                .sum(); // 그리고 더해라

        // 필수 교육 별 통계 DTO 변환
        // incompleteTrainings.results() 안에 교육별 미수료 현황이 들어 있다.
        // IncompleteTrainingResponse.Result(trainingService 반환타입)를 RequiredCourseStatResponse 바꾼다.
        List<RequiredCourseStatResponse> requiredCourseStats = incompleteTrainings.results().stream()
                .map(result -> new RequiredCourseStatResponse( // 교육 관리의 응답을 대시보드에 쓸거라 그거에 맞게 바꿔줌
                        // 반환되는 값
                        result.courseId(),
                        result.courseTitle(),
                        result.incompleteCount(),
                        result.completionRate()
                ))
                .toList();

        return new DashboardResponse(
                employeeCount,
                courseCount,
                requiredCourseCount,
                overallCompletionRate,
                totalIncompleteEmployeeCount,
                requiredCourseStats // 교육 아이디, 제목, 미수료자, 수료 비율
        );
    }

    // 전체 교육 이수율을 계산
    private int calculateOverallCompletionRate(IncompleteTrainingResponse incompleteTrainings) {
        int targetEmployeeCount = incompleteTrainings.results().stream()
                .mapToInt(IncompleteTrainingResponse.Result::targetEmployeeCount)
                .sum();

        if (targetEmployeeCount == 0) { // 교육 대상이 없으면 0
            return 0;
        }

        int incompleteCount = incompleteTrainings.results().stream()
                .mapToInt(IncompleteTrainingResponse.Result::incompleteCount)
                .sum();
        int completedCount = targetEmployeeCount - incompleteCount;

        // 이수율 계산
        return (int) Math.round((completedCount * 100.0) / targetEmployeeCount);
    }
}
