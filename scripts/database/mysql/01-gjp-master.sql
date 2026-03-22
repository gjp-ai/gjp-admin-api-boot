-- Use the new database
USE gjp_db;

CREATE TABLE IF NOT EXISTS master_app_settings (
    id CHAR(36) NOT NULL COMMENT 'Primary Key (UUID)',
    name VARCHAR(50) NOT NULL COMMENT 'Setting name (unique identifier)',
    value VARCHAR(500) DEFAULT NULL COMMENT 'Setting value',

    -- Internationalization support
    lang ENUM('EN', 'ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the setting',

    -- Configuration properties
    is_system BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'System config (not user editable)',
    is_public BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Public config (visible to non-admin users)',

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    created_by CHAR(36) DEFAULT NULL COMMENT 'Created by user ID',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    updated_by CHAR(36) DEFAULT NULL COMMENT 'Last updated by user ID',

    PRIMARY KEY (id),
    UNIQUE KEY uk_master_app_settings_name_lang (name, lang),
    KEY idx_system_configs_is_public (is_public),
    KEY idx_system_configs_is_system (is_system),
    KEY idx_system_configs_created_by (created_by),
    KEY idx_system_configs_updated_by (updated_by),

    -- Foreign key constraints
    CONSTRAINT fk_system_configs_created_by FOREIGN KEY (created_by) REFERENCES auth_users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_system_configs_updated_by FOREIGN KEY (updated_by) REFERENCES auth_users (id) ON DELETE SET NULL ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Application settings with internationalization support';

