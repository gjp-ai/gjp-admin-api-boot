package org.ganjp.api.cms.file;

import jakarta.validation.constraints.NotBlank;
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
public class FileCreateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 500, message = "Original URL must not exceed 500 characters")
    private String originalUrl;

    @Size(max = 255, message = "Source name must not exceed 255 characters")
    private String sourceName;

    private MultipartFile file;

    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    private FileAsset.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
