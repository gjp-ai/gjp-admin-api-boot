package org.ganjp.api.edu.freetextquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeTextQuestionResponse {

    private String id;
    private String question;
    private String answer;
    private String description;
    private String questionA;
    private String answerA;
    private String questionB;
    private String answerB;
    private String questionC;
    private String answerC;
    private String questionD;
    private String answerD;
    private String questionE;
    private String answerE;
    private String questionF;
    private String answerF;
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
    private FreeTextQuestion.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static FreeTextQuestionResponse from(FreeTextQuestion question) {
        if (question == null) {
            return null;
        }
        return FreeTextQuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .description(question.getDescription())
                .questionA(question.getQuestionA())
                .answerA(question.getAnswerA())
                .questionB(question.getQuestionB())
                .answerB(question.getAnswerB())
                .questionC(question.getQuestionC())
                .answerC(question.getAnswerC())
                .questionD(question.getQuestionD())
                .answerD(question.getAnswerD())
                .questionE(question.getQuestionE())
                .answerE(question.getAnswerE())
                .questionF(question.getQuestionF())
                .answerF(question.getAnswerF())
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
