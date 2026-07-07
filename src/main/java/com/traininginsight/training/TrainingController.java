package com.traininginsight.training;

import com.traininginsight.training.dto.IncompleteTrainingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 교육별 미수료자 조회 API 함수 fetchIncompleteTrainings()
// IncompletePage.jsx GET /api/trainings/incomplete
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/incomplete")
    public IncompleteTrainingResponse getIncompleteTrainings() {
        return trainingService.getIncompleteTrainings();
    }
}
