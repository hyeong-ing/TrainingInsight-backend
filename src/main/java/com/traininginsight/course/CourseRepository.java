package com.traininginsight.course;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 강의 데이터 찾고 오름차순 정렬
    List<Course> findAllByOrderByIdAsc();

    // 강의 중 필수로 들어야하는 데이터 찾고 오름차순 정렬 (required가 true인 경우 가져오라는 것)
    List<Course> findByRequiredTrueOrderByIdAsc();

    // 필수 교육 개수 조회 -> dashboard 서비스에서 이용 중
    long countByRequiredTrue();
}
