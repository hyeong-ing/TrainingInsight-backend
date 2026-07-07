package com.traininginsight.ai;

import com.traininginsight.ai.dto.AiCourseSearchRequest;
import com.traininginsight.ai.dto.AiCourseSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiSearchController {

    private final AiSearchService aiSearchService;

    @PostMapping("/course-search")
    public AiCourseSearchResponse searchCourses(@Valid @RequestBody AiCourseSearchRequest request) {
        return aiSearchService.searchCourses(request.query());
    }
}
