-- ============================================================================
-- Migration: Add 'channel' column (VARCHAR(20)) to all CMS and Master tables
-- Database: gjp_db
-- Date: 2026-05-19
-- Description: Adds multi-channel support to every CMS and Master table
-- ============================================================================

USE gjp_db;

-- ---------------------------------------------------------------------------
-- CMS Tables
-- ---------------------------------------------------------------------------

-- cms_website
ALTER TABLE `cms_website`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_website_channel` (`channel`);

-- cms_logo
ALTER TABLE `cms_logo`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_logo_channel` (`channel`);

-- cms_image
ALTER TABLE `cms_image`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_image_channel` (`channel`);

-- cms_video
ALTER TABLE `cms_video`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_video_channel` (`channel`);

-- cms_audio
ALTER TABLE `cms_audio`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_audio_channel` (`channel`);

-- cms_article
ALTER TABLE `cms_article`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_article_channel` (`channel`);

-- cms_file
ALTER TABLE `cms_file`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_file_channel` (`channel`);

-- cms_article_image
ALTER TABLE `cms_article_image`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_article_image_channel` (`channel`);

-- cms_question
ALTER TABLE `cms_question`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_cms_question_channel` (`channel`);

-- ---------------------------------------------------------------------------
-- Master Tables
-- ---------------------------------------------------------------------------

-- master_app_settings
ALTER TABLE `master_app_settings`
  ADD COLUMN `channel` VARCHAR(20) DEFAULT NULL COMMENT 'Channel identifier for multi-channel support' AFTER `lang`,
  ADD INDEX `idx_master_app_settings_channel` (`channel`);
