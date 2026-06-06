package org.ganjp.api.edu.truefalsequestion;

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
@Table(name = "edu_true_false_question")
public class TrueFalseQuestion extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    @Column(name = "question", length = 500, nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer", nullable = false)
    private Answer answer;

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

    @Column(name = "topic", length = 100)
    private String topic;

    @Column(name = "term")
    private Integer term;

    @Column(name = "week")
    private Integer week;

    @Column(name = "channel", length = 20, nullable = false)
    @Builder.Default
    private String channel = "All";

    @Column(name = "tags", length = 500)
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

    public enum Answer {
        TRUE,
        FALSE
    }

    public enum Language {
        EN,
        ZH
    }

    public boolean isActiveTrueFalseQuestion() {
        return isActive != null && isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrueFalseQuestion that = (TrueFalseQuestion) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
