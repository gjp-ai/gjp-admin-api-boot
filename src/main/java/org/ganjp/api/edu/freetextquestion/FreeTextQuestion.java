package org.ganjp.api.edu.freetextquestion;

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
@Table(name = "edu_free_text_question")
public class FreeTextQuestion extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    @Column(name = "question", length = 500, nullable = false)
    private String question;

    @Column(name = "answer", length = 500)
    private String answer;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "question_a", length = 500)
    private String questionA;

    @Column(name = "answer_a", length = 500)
    private String answerA;

    @Column(name = "question_b", length = 500)
    private String questionB;

    @Column(name = "answer_b", length = 500)
    private String answerB;

    @Column(name = "question_c", length = 500)
    private String questionC;

    @Column(name = "answer_c", length = 500)
    private String answerC;

    @Column(name = "question_d", length = 500)
    private String questionD;

    @Column(name = "answer_d", length = 500)
    private String answerD;

    @Column(name = "question_e", length = 500)
    private String questionE;

    @Column(name = "answer_e", length = 500)
    private String answerE;

    @Column(name = "question_f", length = 500)
    private String questionF;

    @Column(name = "answer_f", length = 500)
    private String answerF;

    @Column(name = "explanation", length = 800)
    private String explanation;

    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;

    @Column(name = "fail_count", nullable = false)
    @Builder.Default
    private Integer failCount = 0;

    @Column(name = "success_count", nullable = false)
    @Builder.Default
    private Integer successCount = 0;

    @Column(name = "grade_level", length = 20)
    private String gradeLevel;

    @Column(name = "subject", length = 20)
    private String subject;

    @Column(name = "topic", length = 20)
    private String topic;

    @Column(name = "term")
    private Integer term;

    @Column(name = "week")
    private Integer week;

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

    public boolean isActiveFreeTextQuestion() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreeTextQuestion that = (FreeTextQuestion) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
