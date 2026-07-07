package com.traininginsight.ai;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AiSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsBadRequestWhenQueryIsBlank() throws Exception {
        mockMvc.perform(post("/api/ai/course-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"query": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestWhenQueryIsLongerThanFiveHundredCharacters() throws Exception {
        String longQuery = "a".repeat(501);

        mockMvc.perform(post("/api/ai/course-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"query": "%s"}
                                """.formatted(longQuery)))
                .andExpect(status().isBadRequest());
    }
}
