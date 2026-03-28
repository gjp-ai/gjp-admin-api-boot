package org.ganjp.api.cms.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String id;
    private String name;
    private String originalUrl;
    private String sourceName;
    private String filename;
    private Long sizeBytes;
    private String extension;
    private String mimeType;
    private String tags;
    private FileAsset.Language lang;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;

    public static FileResponse from(FileAsset file) {
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .originalUrl(file.getOriginalUrl())
                .sourceName(file.getSourceName())
                .filename(file.getFilename())
                .sizeBytes(file.getSizeBytes())
                .extension(file.getExtension())
                .mimeType(file.getMimeType())
                .tags(file.getTags())
                .lang(file.getLang())
                .displayOrder(file.getDisplayOrder())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .createdBy(file.getCreatedBy())
                .updatedBy(file.getUpdatedBy())
                .isActive(file.getIsActive())
                .build();
    }
}
