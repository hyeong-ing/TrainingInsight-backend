package com.traininginsight.course;

import com.traininginsight.course.dto.CourseSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 교육 검색 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/search")
    public CourseSearchResponse searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean required,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String hireType
    ) {
        return courseService.searchCourses(keyword, category, required, department, hireType);
    }
}
