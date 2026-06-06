package org.ganjp.api.edu.questionimage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionImageRepository extends JpaRepository<QuestionImage, String> {

    List<QuestionImage> findByMultipleChoiceQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(String multipleChoiceQuestionId);

    List<QuestionImage> findByFreeTextQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(String freeTextQuestionId);

    List<QuestionImage> findByTrueFalseQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(String trueFalseQuestionId);

    List<QuestionImage> findByFillBlankQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(String fillBlankQuestionId);

    Optional<QuestionImage> findByIdAndIsActiveTrue(String id);

    boolean existsByFilename(String filename);

    @Query("SELECT i FROM QuestionImage i WHERE " +
            "(:multipleChoiceQuestionId IS NULL OR TRIM(:multipleChoiceQuestionId) = '' OR i.multipleChoiceQuestionId = :multipleChoiceQuestionId) AND " +
            "(:freeTextQuestionId IS NULL OR TRIM(:freeTextQuestionId) = '' OR i.freeTextQuestionId = :freeTextQuestionId) AND " +
            "(:trueFalseQuestionId IS NULL OR TRIM(:trueFalseQuestionId) = '' OR i.trueFalseQuestionId = :trueFalseQuestionId) AND " +
            "(:fillBlankQuestionId IS NULL OR TRIM(:fillBlankQuestionId) = '' OR i.fillBlankQuestionId = :fillBlankQuestionId) AND " +
            "(:lang IS NULL OR i.lang = :lang) AND " +
            "(:isActive IS NULL OR i.isActive = :isActive) " +
            "ORDER BY i.displayOrder")
    List<QuestionImage> searchQuestionImages(
            @Param("multipleChoiceQuestionId") String multipleChoiceQuestionId,
            @Param("freeTextQuestionId") String freeTextQuestionId,
            @Param("trueFalseQuestionId") String trueFalseQuestionId,
            @Param("fillBlankQuestionId") String fillBlankQuestionId,
            @Param("lang") QuestionImage.Language lang,
            @Param("isActive") Boolean isActive
    );
}
