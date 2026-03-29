package org.ganjp.api.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Shared CMS configuration properties.
 * Provides the base URL used to construct file URLs in API responses.
 */
@Configuration
@ConfigurationProperties(prefix = "cms")
@Data
public class CmsProperties {
    private String baseUrl;
}
