package org.ganjp.api.edu.phrase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhraseCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String phonetic;
    private String phoneticAudioFilename;
    private String phoneticAudioOriginalUrl;
    private MultipartFile phoneticAudioFile;
    private String synonyms;
    private String translation;
    private String meaningClue;
    private String meaning;
    private String easyMeaning;
    private String sentenceOne;
    private String sentenceTwo;
    private String difficultyLevel;
    private String dictionaryUrl;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;

    @NotNull(message = "Language is required")
    private Phrase.Language lang;

    private Integer displayOrder;
    private Boolean isActive;
}
