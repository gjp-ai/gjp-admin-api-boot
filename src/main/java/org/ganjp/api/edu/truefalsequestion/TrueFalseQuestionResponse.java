package org.ganjp.api.edu.truefalsequestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrueFalseQuestionResponse {

    private String id;
    private String question;
    private TrueFalseQuestion.Answer answer;
    private String explanation;
    private String difficultyLevel;
    private Integer failCount;
    private Integer successCount;
    private String gradeLevel;
    private String subject;
    private String topic;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private TrueFalseQuestion.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static TrueFalseQuestionResponse from(TrueFalseQuestion question) {
        if (question == null) {
            return null;
        }
        return TrueFalseQuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .explanation(question.getExplanation())
                .difficultyLevel(question.getDifficultyLevel())
                .failCount(question.getFailCount())
                .successCount(question.getSuccessCount())
                .gradeLevel(question.getGradeLevel())
                .subject(question.getSubject())
                .topic(question.getTopic())
                .term(question.getTerm())
                .week(question.getWeek())
                .channel(question.getChannel())
                .tags(question.getTags())
                .lang(question.getLang())
                .displayOrder(question.getDisplayOrder())
                .isActive(question.getIsActive())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .build();
    }
}
