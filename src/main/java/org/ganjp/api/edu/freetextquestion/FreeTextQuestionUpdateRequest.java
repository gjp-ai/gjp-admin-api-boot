package org.ganjp.api.edu.freetextquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeTextQuestionUpdateRequest {

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
}
