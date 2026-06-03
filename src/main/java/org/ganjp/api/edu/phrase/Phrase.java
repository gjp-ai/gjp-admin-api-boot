package org.ganjp.api.edu.phrase;

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
@Table(name = "edu_phrase")
public class Phrase extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "phonetic", length = 200)
    private String phonetic;

    @Column(name = "phonetic_audio_filename", length = 60)
    private String phoneticAudioFilename;

    @Column(name = "phonetic_audio_original_url", length = 256)
    private String phoneticAudioOriginalUrl;

    @Column(name = "synonyms", length = 200)
    private String synonyms;

    @Column(name = "translation", length = 200)
    private String translation;

    @Column(name = "meaning_clue", length = 300)
    private String meaningClue;

    @Column(name = "meaning", length = 300)
    private String meaning;

    @Column(name = "easy_meaning", length = 128)
    private String easyMeaning;

    @Column(name = "sentence_one", length = 300)
    private String sentenceOne;

    @Column(name = "sentence_two", length = 300)
    private String sentenceTwo;

    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;

    @Column(name = "dictionary_url", length = 256)
    private String dictionaryUrl;

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

    public boolean isActivePhrase() {
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
        Phrase that = (Phrase) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
