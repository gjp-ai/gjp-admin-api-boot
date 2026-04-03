package org.ganjp.api.cms.audio;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioUpdateRequest {
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String filename; // optional if external

    @Size(max = 500)
    private String originalUrl;

    @Size(max = 255)
    private String sourceName;

    @Size(max = 500)
    private String coverImageFilename;

    // allow uploading a new cover image when updating
    private MultipartFile coverImageFile;

    @Size(max = 2000)
    private String description;

    @Size(max = 255)
    private String subtitle;

    @Size(max = 255)
    private String artist;

    @Size(max = 500)
    private String tags;

    private Audio.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
