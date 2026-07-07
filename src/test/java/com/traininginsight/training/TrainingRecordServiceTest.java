package com.traininginsight.training;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.traininginsight.training.dto.IncompleteTrainingResponse.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class TrainingRecordServiceTest {

    @Autowired
    private TrainingRecordService trainingRecordService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    @Test
    void savesCompletedRecordWhenCompletedIsTrue() {
        trainingRecordService.updateCompletion(2L, 1L, true);

        assertThat(trainingRecordRepository.findAllByEmployeeIdAndCourseId(2L, 1L))
                .allMatch(record -> record.getStatus() == TrainingStatus.COMPLETED);
    }

    @Test
    void savesNotStartedRecordWhenCompletedIsFalse() {
        trainingRecordService.updateCompletion(1L, 1L, false);

        assertThat(trainingRecordRepository.findAllByEmployeeIdAndCourseId(1L, 1L))
                .allMatch(record -> record.getStatus() == TrainingStatus.NOT_STARTED);
    }

    @Test
    void rejectsCourseWhenEmployeeIsNotTarget() {
        assertThatThrownBy(() -> trainingRecordService.updateCompletion(1L, 12L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");
    }

    @Test
    void recalculatesIncompleteResultsAfterCompletionChange() {
        Result before = findIncompleteResult(1L);

        trainingRecordService.updateCompletion(2L, 1L, true);

        Result after = findIncompleteResult(1L);
        assertThat(after.incompleteCount()).isEqualTo(before.incompleteCount() - 1);
        assertThat(employeeIds(after)).doesNotContain(2L);
    }

    private Result findIncompleteResult(Long courseId) {
        return trainingService.getIncompleteTrainings().results().stream()
                .filter(result -> result.courseId().equals(courseId))
                .findFirst()
                .orElseThrow();
    }

    private java.util.List<Long> employeeIds(Result result) {
        return result.employees().stream()
                .map(employee -> employee.employeeId())
                .toList();
    }
}
