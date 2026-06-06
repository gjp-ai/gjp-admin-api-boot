package org.ganjp.api.edu.multiplechoicequestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ganjp.api.common.model.BaseEntity;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "edu_multiple_choice_question")
public class MultipleChoiceQuestion extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    @Column(name = "question", length = 500, nullable = false)
    private String question;

    @Column(name = "option_a", length = 200)
    private String optionA;

    @Column(name = "option_b", length = 200)
    private String optionB;

    @Column(name = "option_c", length = 200)
    private String optionC;

    @Column(name = "option_d", length = 200)
    private String optionD;

    @Column(name = "answer", length = 10, nullable = false)
    private String answer;

    @Column(name = "explanation", length = 800)
    private String explanation;

    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;

    @Column(name = "grade_level", length = 20)
    private String gradeLevel;

    @Column(name = "subject", length = 20)
    private String subject;

    @Column(name = "topic", length = 100)
    private String topic;

    @Column(name = "term")
    private Integer term;

    @Column(name = "week")
    private Integer week;

    @Column(name = "fail_count", nullable = false)
    @Builder.Default
    private Integer failCount = 0;

    @Column(name = "success_count", nullable = false)
    @Builder.Default
    private Integer successCount = 0;

    @Column(name = "channel", length = 20, nullable = false)
    @Builder.Default
    private String channel = "All";

    @Column(name = "tags", length = 100)
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", length = 2, nullable = false)
    @Builder.Default
    private Language lang = Language.EN;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public enum Language {
        EN,
        ZH
    }

    public boolean isActiveMultipleChoiceQuestion() {
        return isActive != null && isActive;
    }

    public String[] getTagsArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    public void setTagsFromArray(String[] tagsArray) {
        if (tagsArray == null || tagsArray.length == 0) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagsArray);
        }
    }

    public String[] getAnswerArray() {
        if (answer == null || answer.trim().isEmpty()) {
            return new String[0];
        }
        return answer.split(",");
    }

    public void setAnswerFromArray(String[] answerArray) {
        if (answerArray == null || answerArray.length == 0) {
            this.answer = null;
        } else {
            this.answer = String.join(",", answerArray);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultipleChoiceQuestion that = (MultipleChoiceQuestion) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
