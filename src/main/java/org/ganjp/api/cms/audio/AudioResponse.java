package org.ganjp.api.cms.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioResponse {
    private String id;
    private String name;
    private String filename;
    private Long sizeBytes;
    private String coverImageFilename;
    private String originalUrl;
    private String sourceName;
    private String subtitle;
    private String description;
    private String artist;
    private String tags;
    private Audio.Language lang;
    private Integer displayOrder;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AudioResponse from(Audio audio) {
        return AudioResponse.builder()
                .id(audio.getId())
                .name(audio.getName())
                .filename(audio.getFilename())
                .sizeBytes(audio.getSizeBytes())
                .coverImageFilename(audio.getCoverImageFilename())
                .originalUrl(audio.getOriginalUrl())
                .sourceName(audio.getSourceName())
                .subtitle(audio.getSubtitle())
                .description(audio.getDescription())
                .artist(audio.getArtist())
                .tags(audio.getTags())
                .lang(audio.getLang())
                .displayOrder(audio.getDisplayOrder())
                .createdBy(audio.getCreatedBy())
                .updatedBy(audio.getUpdatedBy())
                .isActive(audio.getIsActive())
                .createdAt(audio.getCreatedAt())
                .updatedAt(audio.getUpdatedAt())
                .build();
    }
}
