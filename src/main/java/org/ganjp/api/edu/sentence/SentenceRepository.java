package org.ganjp.api.edu.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, String> {

    List<Sentence> findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(String channel, Sentence.Language lang);

    boolean existsByNameAndChannelAndLang(String name, String channel, Sentence.Language lang);

    boolean existsByPhoneticAudioFilenameAndIsActiveTrue(String phoneticAudioFilename);

    @Query("SELECT COUNT(s) > 0 FROM Sentence s WHERE s.name = :name AND s.channel = :channel AND s.lang = :lang AND s.id != :excludeId")
    boolean existsByNameAndChannelAndLangExcludingId(
            @Param("name") String name,
            @Param("channel") String channel,
            @Param("lang") Sentence.Language lang,
            @Param("excludeId") String excludeId
    );

    @Query("SELECT s FROM Sentence s WHERE " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR s.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:channel IS NULL OR TRIM(:channel) = '' OR LOWER(s.channel) LIKE LOWER(CONCAT('%', :channel, '%'))) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND " +
            "(:term IS NULL OR s.term = :term) AND " +
            "(:week IS NULL OR s.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR s.difficultyLevel = :difficultyLevel)")
    Page<Sentence> search(
            @Param("name") String name,
            @Param("lang") Sentence.Language lang,
            @Param("tags") String tags,
            @Param("channel") String channel,
            @Param("isActive") Boolean isActive,
            @Param("term") Integer term,
            @Param("week") Integer week,
            @Param("difficultyLevel") String difficultyLevel,
            Pageable pageable
    );
}
