package com.traininginsight.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 강의 엔티티
@Getter
@Entity
@Table(name = "courses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 제목

    @Column(nullable = false, length = 100)
    private String category; // 카테고리

    @Column(nullable = false, length = 1000)
    private String description; // 설명

    @Column(name = "is_required", nullable = false)
    private boolean required; //필수

    @Column(nullable = false, length = 100)
    private String targetDepartment; // 들어야하는 부서

    @Column(nullable = false, length = 50)
    private String targetHireType; // 들어야하는 직무(대리, 사원 등)
}
