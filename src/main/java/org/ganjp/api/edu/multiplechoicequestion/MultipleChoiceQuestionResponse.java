package org.ganjp.api.edu.multiplechoicequestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuestionResponse {

    private String id;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explanation;
    private String difficultyLevel;
    private String gradeLevel;
    private String subject;
    private String topic;
    private Integer term;
    private Integer week;
    private Integer failCount;
    private Integer successCount;
    private String channel;
    private String tags;
    private MultipleChoiceQuestion.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static MultipleChoiceQuestionResponse from(MultipleChoiceQuestion question) {
        if (question == null) {
            return null;
        }
        return MultipleChoiceQuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .answer(question.getAnswer())
                .explanation(question.getExplanation())
                .difficultyLevel(question.getDifficultyLevel())
                .gradeLevel(question.getGradeLevel())
                .subject(question.getSubject())
                .topic(question.getTopic())
                .term(question.getTerm())
                .week(question.getWeek())
                .failCount(question.getFailCount())
                .successCount(question.getSuccessCount())
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
