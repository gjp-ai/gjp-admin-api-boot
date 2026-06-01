package org.ganjp.api.edu.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyUpdateRequest {

    private String name;
    private String phoneticUs;
    private String phoneticUsAudioFilename;
    private String phoneticUsAudioOriginalUrl;
    private MultipartFile phoneticUsAudioFile;
    private String phoneticUk;
    private String phoneticUkAudioFilename;
    private String phoneticUkAudioOriginalUrl;
    private MultipartFile phoneticUkAudioFile;
    private String partOfSpeech;
    private String synonyms;
    private String translation;
    private String meaningClue;
    private String meaning;
    private String easyMeaning;
    private String sentenceOne;
    private String sentenceTwo;
    private String difficultyLevel;
    private String dictionaryUrl;
    private String additionalInfo;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private Vocabulary.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
