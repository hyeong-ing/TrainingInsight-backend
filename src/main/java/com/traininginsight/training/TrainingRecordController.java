package com.traininginsight.training;

import com.traininginsight.training.dto.TrainingCompletionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 교육 이수 상태 변경 API 함수 PATCH /api/training-records/completion
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-records")
public class TrainingRecordController {

    private final TrainingRecordService trainingRecordService;

    @PatchMapping("/completion")
    public void updateCompletion(@Valid @RequestBody TrainingCompletionRequest request) {
        trainingRecordService.updateCompletion(request.employeeId(), request.courseId(), request.completed());
    }
}
