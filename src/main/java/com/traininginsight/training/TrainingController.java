package com.traininginsight.training;

import com.traininginsight.training.dto.IncompleteTrainingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
