package org.ganjp.api.edu.questionimage;

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
@Table(name = "edu_question_image")
public class QuestionImage extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    @Column(name = "multiple_choice_question_id", columnDefinition = "char(36)")
    private String multipleChoiceQuestionId;

    @Column(name = "free_text_question_id", columnDefinition = "char(36)")
    private String freeTextQuestionId;

    @Column(name = "true_false_question_id", columnDefinition = "char(36)")
    private String trueFalseQuestionId;

    @Column(name = "fill_blank_question_id", columnDefinition = "char(36)")
    private String fillBlankQuestionId;

    @Column(name = "filename", length = 60, nullable = false)
    private String filename;

    @Column(name = "original_url", length = 256)
    private String originalUrl;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionImage that = (QuestionImage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
