package org.ganjp.api.master.setting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.master.setting.AppSettingResponse;
import org.ganjp.api.master.setting.AppSettingCreateRequest;
import org.ganjp.api.master.setting.AppSettingUpdateRequest;
import org.ganjp.api.master.setting.AppSetting;
import org.ganjp.api.master.setting.AppSettingRepository;
import org.ganjp.api.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for App Settings management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppSettingService {

    private final AppSettingRepository appSettingRepository;

    /**
     * Get setting by ID
     */
    public AppSettingResponse getSettingById(String id) {
        AppSetting setting = appSettingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("App setting not found with id: " + id));
        return AppSettingResponse.fromEntity(setting);
    }

    /**
     * Get setting by name, language, and channel
     */
    public AppSettingResponse getSettingByNameAndLang(String name, AppSetting.Language lang, String channel) {
        AppSetting setting = appSettingRepository.findByNameAndLangAndChannel(name, lang, channel)
                .orElseThrow(() -> new BusinessException("App setting not found with name: " + name + ", language: " + lang + " and channel: " + channel));
        return AppSettingResponse.fromEntity(setting);
    }

    /**
     * Get setting value by name, language, and channel
     */
    public String getSettingValue(String name, AppSetting.Language lang, String channel) {
        return appSettingRepository.findByNameAndLangAndChannel(name, lang, channel)
                .map(AppSetting::getValue)
                .orElse(null);
    }

    /**
     * Get setting value by name, language, and channel with default
     */
    public String getSettingValue(String name, AppSetting.Language lang, String channel, String defaultValue) {
        String value = getSettingValue(name, lang, channel);
        return value != null ? value : defaultValue;
    }

    /**
     * Get all settings for a specific name (all languages)
     */
    public List<AppSettingResponse> getSettingsByName(String name) {
        List<AppSetting> settings = appSettingRepository.findByNameOrderByLang(name);
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get settings by language
     */
    public List<AppSettingResponse> getSettingsByLang(AppSetting.Language lang) {
        List<AppSetting> settings = appSettingRepository.findByLangOrderByName(lang);
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get public settings (visible to non-admin users)
     */
    public List<AppSettingResponse> getPublicSettings() {
        List<AppSetting> settings = appSettingRepository.findByIsPublicTrueOrderByNameAscLangAsc();
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get public settings by language
     */
    public List<AppSettingResponse> getPublicSettingsByLang(AppSetting.Language lang) {
        List<AppSetting> settings = appSettingRepository.findByIsPublicTrueAndLangOrderByName(lang);
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user-editable settings
     */
    public List<AppSettingResponse> getUserEditableSettings() {
        List<AppSetting> settings = appSettingRepository.findByIsSystemFalseOrderByNameAscLangAsc();
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user-editable settings by language
     */
    public List<AppSettingResponse> getUserEditableSettingsByLang(AppSetting.Language lang) {
        List<AppSetting> settings = appSettingRepository.findByIsSystemFalseAndLangOrderByName(lang);
        return settings.stream()
                .map(AppSettingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all settings with pagination and filtering
     */
    public Page<AppSettingResponse> getSettings(String searchTerm, AppSetting.Language lang, String channel,
                                               Boolean isPublic, Boolean isSystem, Pageable pageable) {
        Page<AppSetting> settings = appSettingRepository.findBySearchCriteria(searchTerm, lang, channel, isPublic, isSystem, pageable);
        return settings.map(AppSettingResponse::fromEntity);
    }

    /**
     * Create new setting
     */
    @Transactional
    public AppSettingResponse createSetting(AppSettingCreateRequest request, String createdBy) {
        // Check if setting already exists
        if (appSettingRepository.existsByNameAndLangAndChannel(request.getName(), request.getLang(), request.getChannel())) {
            throw new BusinessException("App setting already exists with name: " + request.getName() + ", language: " + request.getLang() + " and channel: " + request.getChannel());
        }

        AppSetting setting = AppSetting.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .value(request.getValue())
                .lang(request.getLang())
                .channel(request.getChannel())
                .isSystem(request.getIsSystem())
                .isPublic(request.getIsPublic())
                .build();

        // Set audit fields manually since they're in BaseEntity
        setting.setCreatedBy(createdBy);
        setting.setUpdatedBy(createdBy);

        AppSetting savedSetting = appSettingRepository.save(setting);
        log.info("Created app setting: {} [{}] by user: {}", savedSetting.getName(), savedSetting.getLang(), createdBy);

        return AppSettingResponse.fromEntity(savedSetting);
    }

    /**
     * Update setting
     */
    @Transactional
    public AppSettingResponse updateSetting(String id, AppSettingUpdateRequest request, String updatedBy) {
        AppSetting setting = appSettingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("App setting not found with id: " + id));

        // Check if setting is user-editable (unless isSystem is being changed)
        if (setting.getIsSystem() && request.getIsSystem() == null) {
            throw new BusinessException("System setting cannot be modified: " + setting.getName());
        }

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(setting.getName())) {
                // Check if the new name+lang+channel combination already exists
                AppSetting.Language langToCheck = request.getLang() != null ? request.getLang() : setting.getLang();
                String channelToCheck = request.getChannel() != null ? request.getChannel() : setting.getChannel();
                appSettingRepository.findByNameAndLangAndChannel(newName, langToCheck, channelToCheck)
                        .ifPresent(existingSetting -> {
                            if (!existingSetting.getId().equals(id)) {
                                throw new BusinessException("App setting already exists with name: " + newName + ", language: " + langToCheck + " and channel: " + channelToCheck);
                            }
                        });
                setting.setName(newName);
            }
        }

        // Update fields if provided
        if (request.getValue() != null) {
            setting.setValue(request.getValue());
        }
        if (request.getLang() != null) {
            // Check if the new name+lang+channel combination already exists for a different setting
            String nameToCheck = request.getName() != null ? request.getName().trim() : setting.getName();
            String channelToCheck = request.getChannel() != null ? request.getChannel() : setting.getChannel();
            appSettingRepository.findByNameAndLangAndChannel(nameToCheck, request.getLang(), channelToCheck)
                    .ifPresent(existingSetting -> {
                        if (!existingSetting.getId().equals(id)) {
                            throw new BusinessException("App setting already exists with name: " + nameToCheck + ", language: " + request.getLang() + " and channel: " + channelToCheck);
                        }
                    });
            setting.setLang(request.getLang());
        }
        if (request.getIsSystem() != null) {
            setting.setIsSystem(request.getIsSystem());
        }
        if (request.getIsPublic() != null) {
            setting.setIsPublic(request.getIsPublic());
        }
        if (request.getChannel() != null) {
            String nameToCheck = request.getName() != null ? request.getName().trim() : setting.getName();
            AppSetting.Language langToCheck = request.getLang() != null ? request.getLang() : setting.getLang();
            appSettingRepository.findByNameAndLangAndChannel(nameToCheck, langToCheck, request.getChannel())
                    .ifPresent(existingSetting -> {
                        if (!existingSetting.getId().equals(id)) {
                            throw new BusinessException("App setting already exists with name: " + nameToCheck + ", language: " + langToCheck + " and channel: " + request.getChannel());
                        }
                    });
            setting.setChannel(request.getChannel());
        }

        setting.setUpdatedBy(updatedBy);
        setting.setUpdatedAt(LocalDateTime.now());

        AppSetting savedSetting = appSettingRepository.save(setting);
        log.info("Updated app setting: {} [{}] by user: {}", savedSetting.getName(), savedSetting.getLang(), updatedBy);

        return AppSettingResponse.fromEntity(savedSetting);
    }

    /**
     * Update setting value only
     */
    @Transactional
    public AppSettingResponse updateSettingValue(String name, AppSetting.Language lang, String channel, String newValue, String updatedBy) {
        AppSetting setting = appSettingRepository.findByNameAndLangAndChannel(name, lang, channel)
                .orElseThrow(() -> new BusinessException("App setting not found with name: " + name + ", language: " + lang + " and channel: " + channel));

        // Check if setting is user-editable
        if (setting.getIsSystem()) {
            throw new BusinessException("System setting cannot be modified: " + setting.getName());
        }

        setting.setValue(newValue);
        setting.setUpdatedBy(updatedBy);
        setting.setUpdatedAt(LocalDateTime.now());

        AppSetting savedSetting = appSettingRepository.save(setting);
        log.info("Updated app setting value: {} [{}] = '{}' by user: {}", savedSetting.getName(), savedSetting.getLang(), newValue, updatedBy);

        return AppSettingResponse.fromEntity(savedSetting);
    }

    /**
     * Delete setting
     */
    @Transactional
    public void deleteSetting(String id, String deletedBy) {
        AppSetting setting = appSettingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("App setting not found with id: " + id));

        // Check if setting is user-editable
        if (setting.getIsSystem()) {
            throw new BusinessException("System setting cannot be deleted: " + setting.getName());
        }

        appSettingRepository.delete(setting);
        log.info("Deleted app setting: {} [{}] by user: {}", setting.getName(), setting.getLang(), deletedBy);
    }

    /**
     * Delete setting by name, language, and channel
     */
    @Transactional
    public void deleteSettingByNameAndLang(String name, AppSetting.Language lang, String channel, String deletedBy) {
        AppSetting setting = appSettingRepository.findByNameAndLangAndChannel(name, lang, channel)
                .orElseThrow(() -> new BusinessException("App setting not found with name: " + name + ", language: " + lang + " and channel: " + channel));

        // Check if setting is user-editable
        if (setting.getIsSystem()) {
            throw new BusinessException("System setting cannot be deleted: " + setting.getName());
        }

        appSettingRepository.deleteByNameAndLangAndChannel(name, lang, channel);
        log.info("Deleted app setting: {} [{}] ({}) by user: {}", name, lang, channel, deletedBy);
    }

    /**
     * Check if setting exists
     */
    public boolean settingExists(String name, AppSetting.Language lang, String channel) {
        return appSettingRepository.existsByNameAndLangAndChannel(name, lang, channel);
    }

    /**
     * Get distinct setting names
     */
    public List<String> getDistinctNames() {
        return appSettingRepository.findDistinctNames();
    }

    /**
     * Get setting statistics
     */
    public Map<String, Object> getSettingStatistics() {
        long totalSettings = appSettingRepository.count();
        long englishSettings = appSettingRepository.countByLang(AppSetting.Language.EN);
        long chineseSettings = appSettingRepository.countByLang(AppSetting.Language.ZH);
        long publicSettings = appSettingRepository.countByIsPublicTrue();
        long systemSettings = appSettingRepository.countByIsSystemTrue();
        List<String> settingNames = getDistinctNames();

        return Map.of(
            "totalSettings", totalSettings,
            "englishSettings", englishSettings,
            "chineseSettings", chineseSettings,
            "publicSettings", publicSettings,
            "systemSettings", systemSettings,
            "userEditableSettings", totalSettings - systemSettings,
            "distinctNames", settingNames.size(),
            "settingNames", settingNames
        );
    }
}
