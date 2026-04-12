package org.ganjp.api.cms.video;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoUpdateRequest {
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

    @Size(max = 500)
    private String tags;

    private Video.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
