package com.traininginsight;

import static org.assertj.core.api.Assertions.assertThat;

import com.traininginsight.course.CourseRepository;
import com.traininginsight.employee.EmployeeRepository;
import com.traininginsight.training.TrainingRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrainingInsightApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void sampleDataLoads() {
        assertThat(employeeRepository.count()).isEqualTo(4);
        assertThat(courseRepository.count()).isEqualTo(4);
        assertThat(trainingRecordRepository.count()).isEqualTo(7);
    }
}
