package org.ganjp.api.edu.phrase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, String> {

    List<Phrase> findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(String channel, Phrase.Language lang);

    boolean existsByNameAndChannelAndLang(String name, String channel, Phrase.Language lang);

    boolean existsByPhoneticAudioFilenameAndIsActiveTrue(String phoneticAudioFilename);

    @Query("SELECT COUNT(p) > 0 FROM Phrase p WHERE p.name = :name AND p.channel = :channel AND p.lang = :lang AND p.id != :excludeId")
    boolean existsByNameAndChannelAndLangExcludingId(
            @Param("name") String name,
            @Param("channel") String channel,
            @Param("lang") Phrase.Language lang,
            @Param("excludeId") String excludeId
    );

    @Query("SELECT p FROM Phrase p WHERE " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR p.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:channel IS NULL OR TRIM(:channel) = '' OR LOWER(p.channel) LIKE LOWER(CONCAT('%', :channel, '%'))) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive) AND " +
            "(:term IS NULL OR p.term = :term) AND " +
            "(:week IS NULL OR p.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR p.difficultyLevel = :difficultyLevel)")
    Page<Phrase> search(
            @Param("name") String name,
            @Param("lang") Phrase.Language lang,
            @Param("tags") String tags,
            @Param("channel") String channel,
            @Param("isActive") Boolean isActive,
            @Param("term") Integer term,
            @Param("week") Integer week,
            @Param("difficultyLevel") String difficultyLevel,
            Pageable pageable
    );
}
