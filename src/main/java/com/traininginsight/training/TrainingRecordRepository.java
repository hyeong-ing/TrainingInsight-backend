package com.traininginsight.training;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 교육 기록 DB 접근 클래스
public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {

    // 교육 상태 수세기
    long countByStatus(TrainingStatus status);

    // 직원 id랑 강의 id 찾기
    List<TrainingRecord> findAllByEmployeeIdAndCourseId(Long employeeId, Long courseId);

    // employee와 course가 LAZY 설정 -> TrainingRecord에 설정되어있음
    // 추가 쿼리 나가니까 join fetch를 사용해서 한번에 가져오도록 한다.
    @Query("""
            select record
            from TrainingRecord record
            join fetch record.employee 
            join fetch record.course
            """)
    List<TrainingRecord> findAllWithEmployeeAndCourse();

    @Query("""
            select record
            from TrainingRecord record
            join fetch record.employee
            join fetch record.course
            where record.status = :status
            """)
    List<TrainingRecord> findAllByStatusWithEmployeeAndCourse(@Param("status") TrainingStatus status);
}
