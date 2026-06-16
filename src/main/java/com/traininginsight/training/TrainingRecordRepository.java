package com.traininginsight.training;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {

    @Query("""
            select record
            from TrainingRecord record
            join fetch record.employee
            join fetch record.course
            where record.status = :status
            """)
    List<TrainingRecord> findAllByStatusWithEmployeeAndCourse(@Param("status") TrainingStatus status);
}
