package org.ganjp.api.edu.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, String> {

    List<Vocabulary> findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(String channel, Vocabulary.Language lang);

    boolean existsByNameAndChannelAndLang(String name, String channel, Vocabulary.Language lang);

    boolean existsByPhoneticUsAudioFilenameAndIsActiveTrue(String phoneticUsAudioFilename);

    boolean existsByPhoneticUkAudioFilenameAndIsActiveTrue(String phoneticUkAudioFilename);

    @Query("SELECT COUNT(v) > 0 FROM Vocabulary v WHERE v.name = :name AND v.channel = :channel AND v.lang = :lang AND v.id != :excludeId")
    boolean existsByNameAndChannelAndLangExcludingId(
            @Param("name") String name,
            @Param("channel") String channel,
            @Param("lang") Vocabulary.Language lang,
            @Param("excludeId") String excludeId
    );

    @Query("SELECT v FROM Vocabulary v WHERE " +
            "(:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR v.lang = :lang) AND " +
            "(:tags IS NULL OR v.tags LIKE CONCAT('%', :tags, '%')) AND " +
            "(:channel IS NULL OR LOWER(v.channel) LIKE LOWER(CONCAT('%', :channel, '%'))) AND " +
            "(:isActive IS NULL OR v.isActive = :isActive) AND " +
            "(:term IS NULL OR v.term = :term) AND " +
            "(:week IS NULL OR v.week = :week) AND " +
            "(:difficultyLevel IS NULL OR v.difficultyLevel = :difficultyLevel) AND " +
            "(:partOfSpeech IS NULL OR LOWER(v.partOfSpeech) LIKE LOWER(CONCAT('%', :partOfSpeech, '%')))")
    Page<Vocabulary> search(
            @Param("name") String name,
            @Param("lang") Vocabulary.Language lang,
            @Param("tags") String tags,
            @Param("channel") String channel,
            @Param("isActive") Boolean isActive,
            @Param("term") Integer term,
            @Param("week") Integer week,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("partOfSpeech") String partOfSpeech,
            Pageable pageable
    );
}
