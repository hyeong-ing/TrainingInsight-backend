package com.traininginsight.ai;

import com.traininginsight.ai.dto.AiSearchCondition;
import java.util.Locale;
import org.springframework.stereotype.Component;


// PoC용 가짜 AI 클라이언트 (실제 LLM 호춡 X, 특정 키워드를 보고 조건을 만든다)
@Component
public class FakeAiClient implements AiClient {

    @Override
    public AiSearchCondition interpretCourseSearchCondition(String query) {
        String keyword = null;
        String category = null;
        Boolean required = null;
        String targetDepartment = null;
        String targetHireType = null;

        String lowerQuery = query.toLowerCase(Locale.ROOT);

        if (query.contains("신입") || query.contains("신입사원")) {
            targetHireType = "신입";
            category = "온보딩";
        }
        if (query.contains("경력")) {
            targetHireType = "경력";
        }
        if (query.contains("필수")) {
            required = true;
        }
        if (query.contains("선택") || query.contains("자율")) {
            required = false;
        }
        if (query.contains("품질") || query.contains("품질관리")) {
            targetDepartment = "품질관리팀";
            category = "품질관리";
        }
        if (query.contains("개발")) {
            targetDepartment = "개발팀";
            category = "개발";
        }
        if (query.contains("인사")) {
            targetDepartment = "인사팀";
        }
        if (query.contains("영업") || query.contains("고객응대") || query.contains("고객 응대")) {
            targetDepartment = "영업팀";
            category = "영업";
        }
        if (query.contains("윤리") || query.contains("윤리경영") || query.contains("준법") || query.contains("컴플라이언스")) {
            keyword = "윤리";
            category = "준법교육";
        }
        if (query.contains("산업안전") || query.contains("안전보건")) {
            keyword = "산업안전";
            category = "법정교육";
        }
        if (query.contains("법정")) {
            category = "법정교육";
        }
        if (lowerQuery.contains("ai") || query.contains("자동화")) {
            category = "AI";
            keyword = "AI";
        }
        if (query.contains("데이터") || query.contains("분석")) {
            category = "데이터";
            keyword = "데이터";
        }
        if (query.contains("리더십") || query.contains("관리자")) {
            category = "리더십";
        }
        if (query.contains("협업") || query.contains("커뮤니케이션") || query.contains("소통")) {
            category = "조직문화";
        }
        if (lowerQuery.contains("esg") || query.contains("지속가능")) {
            category = "ESG";
        }
        if (query.contains("식품안전")) {
            keyword = "식품안전";
        }
        if (query.contains("개인정보") || query.contains("보호")) {
            keyword = "개인정보";
        }
        if (query.contains("보안코딩") || query.contains("보안 코딩")) {
            targetDepartment = "개발팀";
            category = "개발";
        }
        if (query.contains("온보딩")) {
            category = "온보딩";
            keyword = "온보딩";
        }

        if (keyword == null && category == null && required == null && targetDepartment == null && targetHireType == null) {
            keyword = query;
        }

        return new AiSearchCondition(keyword, category, required, targetDepartment, targetHireType);
    }
}
