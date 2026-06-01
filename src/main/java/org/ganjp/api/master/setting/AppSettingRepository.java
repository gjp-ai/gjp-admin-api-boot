package org.ganjp.api.master.setting;

import org.ganjp.api.master.setting.AppSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for App Settings management
 */
@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, String> {

    /**
     * Find setting by name, language, and channel.
     */
    Optional<AppSetting> findByNameAndLangAndChannel(String name, AppSetting.Language lang, String channel);

    /**
     * Find settings by name (all languages)
     */
    List<AppSetting> findByNameOrderByLang(String name);

    /**
     * Find settings by language
     */
    List<AppSetting> findByLangOrderByName(AppSetting.Language lang);

    /**
     * Find public settings (visible to non-admin users)
     */
    List<AppSetting> findByIsPublicTrueOrderByNameAscLangAsc();

    /**
     * Find public settings by channel.
     */
    List<AppSetting> findByIsPublicTrueAndChannelOrderByNameAscLangAsc(String channel);

    /**
     * Find public settings by language
     */
    List<AppSetting> findByIsPublicTrueAndLangOrderByName(AppSetting.Language lang);

    /**
     * Find public setting by name, language, and channel.
     */
    Optional<AppSetting> findByNameAndLangAndChannelAndIsPublicTrue(String name, AppSetting.Language lang, String channel);

    /**
     * Find user-editable settings (non-system configs)
     */
    List<AppSetting> findByIsSystemFalseOrderByNameAscLangAsc();

    /**
     * Find user-editable settings by language
     */
    List<AppSetting> findByIsSystemFalseAndLangOrderByName(AppSetting.Language lang);

    /**
     * Search settings by name or value
     */
    @Query("SELECT a FROM AppSetting a WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.value) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:lang IS NULL OR a.lang = :lang) " +
           "AND (:channel IS NULL OR LOWER(a.channel) LIKE LOWER(CONCAT('%', :channel, '%'))) " +
           "AND (:isPublic IS NULL OR a.isPublic = :isPublic) " +
           "AND (:isSystem IS NULL OR a.isSystem = :isSystem) " +
           "ORDER BY a.name, a.lang")
    Page<AppSetting> findBySearchCriteria(
            @Param("searchTerm") String searchTerm,
            @Param("lang") AppSetting.Language lang,
            @Param("channel") String channel,
            @Param("isPublic") Boolean isPublic,
            @Param("isSystem") Boolean isSystem,
            Pageable pageable);

    /**
     * Check if setting exists by name, language, and channel.
     */
    boolean existsByNameAndLangAndChannel(String name, AppSetting.Language lang, String channel);

    /**
     * Count settings by language
     */
    long countByLang(AppSetting.Language lang);

    /**
     * Count public settings
     */
    long countByIsPublicTrue();

    /**
     * Count system settings
     */
    long countByIsSystemTrue();

    /**
     * Delete by name, language, and channel.
     */
    void deleteByNameAndLangAndChannel(String name, AppSetting.Language lang, String channel);

    /**
     * Find distinct setting names
     */
    @Query("SELECT DISTINCT a.name FROM AppSetting a ORDER BY a.name")
    List<String> findDistinctNames();
}
