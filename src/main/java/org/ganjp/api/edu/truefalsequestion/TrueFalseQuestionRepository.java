package org.ganjp.api.edu.truefalsequestion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrueFalseQuestionRepository extends JpaRepository<TrueFalseQuestion, String> {

    List<TrueFalseQuestion> findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(String channel, TrueFalseQuestion.Language lang);

    boolean existsByQuestionAndChannelAndLang(String question, String channel, TrueFalseQuestion.Language lang);

    @Query("SELECT COUNT(q) > 0 FROM TrueFalseQuestion q WHERE q.question = :question AND q.channel = :channel AND q.lang = :lang AND q.id != :excludeId")
    boolean existsByQuestionAndChannelAndLangExcludingId(
            @Param("question") String question,
            @Param("channel") String channel,
            @Param("lang") TrueFalseQuestion.Language lang,
            @Param("excludeId") String excludeId
    );

    @Query("SELECT q FROM TrueFalseQuestion q WHERE " +
            "(:question IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :question, '%'))) AND " +
            "(:lang IS NULL OR q.lang = :lang) AND " +
            "(:tags IS NULL OR q.tags LIKE CONCAT('%', :tags, '%')) AND " +
            "(:channel IS NULL OR LOWER(q.channel) LIKE LOWER(CONCAT('%', :channel, '%'))) AND " +
            "(:isActive IS NULL OR q.isActive = :isActive) AND " +
            "(:term IS NULL OR q.term = :term) AND " +
            "(:week IS NULL OR q.week = :week) AND " +
            "(:difficultyLevel IS NULL OR q.difficultyLevel = :difficultyLevel) AND " +
            "(:gradeLevel IS NULL OR q.gradeLevel = :gradeLevel) AND " +
            "(:subject IS NULL OR q.subject = :subject) AND " +
            "(:topic IS NULL OR q.topic = :topic)")
    Page<TrueFalseQuestion> search(
            @Param("question") String question,
            @Param("lang") TrueFalseQuestion.Language lang,
            @Param("tags") String tags,
            @Param("channel") String channel,
            @Param("isActive") Boolean isActive,
            @Param("term") Integer term,
            @Param("week") Integer week,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("gradeLevel") String gradeLevel,
            @Param("subject") String subject,
            @Param("topic") String topic,
            Pageable pageable
    );
}
