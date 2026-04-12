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
    private String fileUrl;
    private Long sizeBytes;
    private String coverImageFilename;
    private String coverImageUrl;
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
    private Audio.DownloadStatus downloadStatus;
    private String downloadError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AudioResponse from(Audio audio, String baseUrl) {
        String fileUrl = null;
        if (audio.getFilename() != null) {
            fileUrl = baseUrl + "/v1/audios/view/" + audio.getFilename();
        }
        String coverUrl = null;
        if (audio.getCoverImageFilename() != null) {
            if (audio.getCoverImageFilename().startsWith("http")) {
                coverUrl = audio.getCoverImageFilename();
            } else {
                coverUrl = baseUrl + "/v1/audios/cover/" + audio.getCoverImageFilename();
            }
        }
        return AudioResponse.builder()
                .id(audio.getId())
                .name(audio.getName())
                .filename(audio.getFilename())
                .fileUrl(fileUrl)
                .sizeBytes(audio.getSizeBytes())
                .coverImageFilename(audio.getCoverImageFilename())
                .coverImageUrl(coverUrl)
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
                .downloadStatus(audio.getDownloadStatus())
                .downloadError(audio.getDownloadError())
                .createdAt(audio.getCreatedAt())
                .updatedAt(audio.getUpdatedAt())
                .build();
    }
}
