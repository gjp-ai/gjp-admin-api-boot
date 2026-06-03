-- Use the new database
USE gjp_db;

CREATE TABLE `edu_vocabulary` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `name` varchar(50) NOT NULL COMMENT 'The vocabulary name',
  
  `phonetic_us` varchar(100) DEFAULT NULL COMMENT 'Phonetic transcription',
  `phonetic_us_audio_filename` varchar(60) DEFAULT NULL COMMENT 'Phonetic audio file path',
  `phonetic_us_audio_original_url` varchar(256) DEFAULT NULL COMMENT 'Phonetic audio original URL',
  `phonetic_uk` varchar(100) DEFAULT NULL COMMENT 'Phonetic transcription',
  `phonetic_uk_audio_filename` varchar(60) DEFAULT NULL COMMENT 'Phonetic audio file path',
  `phonetic_uk_audio_original_url` varchar(256) DEFAULT NULL COMMENT 'Phonetic audio original URL',

  `part_of_speech` varchar(20) DEFAULT NULL COMMENT 'Part of speech (noun, verb, etc.)',
  `synonyms` varchar(60) DEFAULT NULL COMMENT 'Comma-separated synonyms',
  `translation` varchar(60) DEFAULT NULL COMMENT 'translation of the word',
  `meaning_clue` varchar(200) DEFAULT NULL COMMENT 'A clue or hint to help remember the meaning of the word',
  `meaning` varchar(200) DEFAULT NULL COMMENT 'Meaning of the word in the target language',
  `easy_meaning` varchar(50) DEFAULT NULL COMMENT 'Simplified meaning or mnemonic for easier recall',
  `sentence_one` varchar(200) DEFAULT NULL COMMENT 'Sentence example 1 using the vocabulary word',
  `sentence_two` varchar(200) DEFAULT NULL COMMENT 'Sentence example 2 using the vocabulary word',

  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the vocabulary',
  `dictionary_url` varchar(256) DEFAULT NULL COMMENT 'Link to an online dictionary entry',
  `additional_info` varchar(500) DEFAULT NULL COMMENT 'Additional information about the vocabulary',
  
  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',
  
  `channel` VARCHAR(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(100) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',
  
  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the website content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',
  
  -- Audit Trail (following your project pattern)
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',
  
  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_vocab_name_channel_lang` (`name`, `channel`, `lang`),

  -- Indexes
  KEY `idx_edu_vocab_name` (`name`),
  KEY `idx_edu_vocab_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_vocab_curriculum` (`channel`, `lang`, `term`, `week`, `display_order`),
  
  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_vocab_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_vocab_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Vocabulary words for language learning';

CREATE TABLE `edu_phrase` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `name` varchar(128) NOT NULL COMMENT 'The phrase text',

  `phonetic` varchar(200) DEFAULT NULL COMMENT 'Phonetic transcription',
  `phonetic_audio_filename` varchar(60) DEFAULT NULL COMMENT 'Phonetic audio file path',
  `phonetic_audio_original_url` varchar(256) DEFAULT NULL COMMENT 'Phonetic audio original URL',

  `synonyms` varchar(200) DEFAULT NULL COMMENT 'Comma-separated similar phrases or expressions',
  `translation` varchar(200) DEFAULT NULL COMMENT 'Translation of the phrase',
  `meaning_clue` varchar(300) DEFAULT NULL COMMENT 'A clue or hint to help remember the meaning of the phrase',
  `meaning` varchar(300) DEFAULT NULL COMMENT 'Meaning of the phrase in the target language',
  `easy_meaning` varchar(128) DEFAULT NULL COMMENT 'Simplified meaning or mnemonic for easier recall',

  `sentence_one` varchar(300) DEFAULT NULL COMMENT 'Sentence example 1 using the phrase',
  `sentence_two` varchar(300) DEFAULT NULL COMMENT 'Sentence example 2 using the phrase',

  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the vocabulary',
  `dictionary_url` varchar(256) DEFAULT NULL COMMENT 'Link to an online dictionary entry',

  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(100) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',

  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the website content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',

  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',

  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_phrase_name_channel_lang` (`name`, `channel`, `lang`),

  KEY `idx_edu_phrase_name` (`name`),
  KEY `idx_edu_phrase_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_phrase_curriculum` (`channel`, `lang`, `term`, `week`, `display_order`),

  CONSTRAINT `fk_edu_phrase_created_by`
    FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`)
    ON DELETE SET NULL ON UPDATE CASCADE,

  CONSTRAINT `fk_edu_phrase_updated_by`
    FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`)
    ON DELETE SET NULL ON UPDATE CASCADE

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Phrases for language learning';


CREATE TABLE `edu_sentence` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `name` varchar(400) NOT NULL COMMENT 'The sentence text',
  `phonetic` varchar(400) DEFAULT NULL COMMENT 'Phonetic transcription',
  `phonetic_audio_filename` varchar(60) DEFAULT NULL COMMENT 'Phonetic audio file path',

  `translation` varchar(400) DEFAULT NULL COMMENT 'Translation of the sentence',
  `explanation` varchar(1000) DEFAULT NULL COMMENT 'Explanation or context of the sentence',

  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the sentence',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(100) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',

  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the website content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',

  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',

  -- Audit Trail
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',

  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_sentence_name_channel_lang` (`name`(255), `channel`, `lang`),

  -- Indexes
  KEY `idx_edu_sentence_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_sentence_curriculum` (`channel`, `lang`, `term`, `week`, `display_order`),
  KEY `idx_edu_sentence_difficulty` (`channel`, `lang`, `difficulty_level`, `is_active`),

  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_sentence_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_sentence_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Sentences for language learning';

-- Multiple Choice Questions Table
CREATE TABLE `edu_multiple_choice_question` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `question` varchar(500) NOT NULL COMMENT 'The question text',
  `option_a` varchar(200) DEFAULT NULL COMMENT 'Option A',
  `option_b` varchar(200) DEFAULT NULL COMMENT 'Option B',
  `option_c` varchar(200) DEFAULT NULL COMMENT 'Option C',
  `option_d` varchar(200) DEFAULT NULL COMMENT 'Option D',
  `answer` varchar(10) NOT NULL COMMENT 'Comma-separated correct answer options (e.g., A,C)',
  `explanation` varchar(800) DEFAULT NULL COMMENT 'Explanation for the correct answer',
  
  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the question',

  `grade_level` varchar(20) DEFAULT NULL COMMENT 'Grade level for curriculum organization',
  `subject` varchar(20) DEFAULT NULL COMMENT 'Subject area (e.g., Grammar, Science)',
  `topic` varchar(20) DEFAULT NULL COMMENT 'Topic associated with the question',
  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',
  
  `fail_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered incorrectly',
  `success_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered correctly',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(100) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',
  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the question content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',
  
  -- Audit Trail
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',
  
  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_mcq_question_channel_lang` (`question`(255), `channel`, `lang`),
  
  -- Indexes
  KEY `idx_edu_mcq_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_mcq_curriculum` (`channel`, `lang`, `grade_level`, `subject`, `topic`, `term`, `week`, `display_order`),
  KEY `idx_edu_mcq_difficulty` (`channel`, `lang`, `difficulty_level`, `is_active`),
  
  -- Foreign Key Constraints
  CONSTRAINT `chk_edu_mcq_answer` CHECK (`answer` REGEXP '^[A-D](,[A-D])*$'),
  CONSTRAINT `fk_edu_mcq_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_mcq_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Multiple choice questions for language learning';

-- Free Text Answer Questions Table
CREATE TABLE `edu_free_text_question` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `question` varchar(500) NOT NULL COMMENT 'The main question text',
  `answer` varchar(500) DEFAULT NULL COMMENT 'The main correct answer',

  `description` varchar(500) DEFAULT NULL COMMENT 'Additional description or context for the questions',

  `question_a` varchar(500) DEFAULT NULL COMMENT 'Alternative question text A',
  `answer_a` varchar(500) DEFAULT NULL COMMENT 'Alternative answer A',
  `question_b` varchar(500) DEFAULT NULL COMMENT 'Alternative question text B',
  `answer_b` varchar(500) DEFAULT NULL COMMENT 'Alternative answer B',
  `question_c` varchar(500) DEFAULT NULL COMMENT 'Alternative question text C',
  `answer_c` varchar(500) DEFAULT NULL COMMENT 'Alternative answer C',
  `question_d` varchar(500) DEFAULT NULL COMMENT 'Alternative question text D',
  `answer_d` varchar(500) DEFAULT NULL COMMENT 'Alternative answer D',
  `question_e` varchar(500) DEFAULT NULL COMMENT 'Alternative question text E',
  `answer_e` varchar(500) DEFAULT NULL COMMENT 'Alternative answer E',
  `question_f` varchar(500) DEFAULT NULL COMMENT 'Alternative question text F',
  `answer_f` varchar(500) DEFAULT NULL COMMENT 'Alternative answer F',

  `explanation` varchar(800) DEFAULT NULL COMMENT 'Explanation for the correct answer',  
  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the question',

  `fail_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered incorrectly',
  `success_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered correctly',

  `grade_level` varchar(20) DEFAULT NULL COMMENT 'Grade level for curriculum organization',
  `subject` varchar(20) DEFAULT NULL COMMENT 'Subject area (e.g., Grammar, Science)',
  `topic` varchar(20) DEFAULT NULL COMMENT 'Topic associated with the question',
  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(100) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',
  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the question content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',
  
  -- Audit Trail
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',
  
  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_ftq_question_channel_lang` (`question`(255), `channel`, `lang`),
  
  -- Indexes
  KEY `idx_edu_ftq_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_ftq_curriculum` (`channel`, `lang`, `grade_level`, `subject`, `topic`, `term`, `week`, `display_order`),
  KEY `idx_edu_ftq_difficulty` (`channel`, `lang`, `difficulty_level`, `is_active`),
  
  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_ftq_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_ftq_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Free text answer questions';

CREATE TABLE `edu_true_false_question` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `question` varchar(500) NOT NULL COMMENT 'The question text',
  `answer` enum('TRUE','FALSE') NOT NULL COMMENT 'Correct answer for the question',
  `explanation` varchar(800) DEFAULT NULL COMMENT 'Explanation for the correct answer',

  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the question',

  `fail_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered incorrectly',
  `success_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered correctly',

  `grade_level` varchar(20) DEFAULT NULL COMMENT 'Grade level for curriculum organization',
  `subject` varchar(20) DEFAULT NULL COMMENT 'Subject area (e.g., Grammar, Science)',
  `topic` varchar(20) DEFAULT NULL COMMENT 'Topic associated with the question',
  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(500) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',
  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the question content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',

  -- Audit Trail
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',

  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_tf_question_channel_lang` (`question`(255), `channel`, `lang`),

  -- Indexes
  KEY `idx_edu_tf_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_tf_curriculum` (`channel`, `lang`, `grade_level`, `subject`, `topic`, `term`, `week`, `display_order`),
  KEY `idx_edu_tf_difficulty` (`channel`, `lang`, `difficulty_level`, `is_active`),

  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_tf_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_tf_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='True/False questions for vocabulary practice';

CREATE TABLE `edu_fill_blank_question` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',
  `question` varchar(500) NOT NULL COMMENT 'Question text with blank(s), e.g. "I ___ to school yesterday."',
  `answer` varchar(200) NOT NULL COMMENT 'Comma-separated correct answers for the blank',
  `explanation` varchar(800) DEFAULT NULL COMMENT 'Explanation for the correct answer(s)',

  `difficulty_level` varchar(20) DEFAULT NULL COMMENT 'Difficulty level of the question',

  `fail_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered incorrectly',
  `success_count` int NOT NULL DEFAULT 0 COMMENT 'Number of times users answered correctly',
  
  `grade_level` varchar(20) DEFAULT NULL COMMENT 'Grade level for curriculum organization',
  `subject` varchar(20) DEFAULT NULL COMMENT 'Subject area (e.g., Grammar, Science)',
  `topic` varchar(20) DEFAULT NULL COMMENT 'Topic associated with the question',
  `term` smallint DEFAULT NULL COMMENT 'Term number for curriculum organization',
  `week` smallint DEFAULT NULL COMMENT 'Week number for curriculum organization',

  `channel` varchar(20) NOT NULL DEFAULT 'All' COMMENT 'Channel identifier for multi-channel support',
  `tags` varchar(500) DEFAULT NULL COMMENT 'Comma-separated tags for categorization and search',
  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Language for the question content',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Order for display (lower = higher priority)',

  -- Audit Trail
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',

  -- Soft Delete
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_fill_blank_question_channel_lang` (`question`(255), `channel`, `lang`),

  -- Indexes
  KEY `idx_edu_fill_blank_channel_lang_active_order` (`channel`, `lang`, `is_active`, `display_order`),
  KEY `idx_edu_fill_blank_curriculum` (`channel`, `lang`, `grade_level`, `subject`, `topic`, `term`, `week`, `display_order`),
  KEY `idx_edu_fill_blank_difficulty` (`channel`, `lang`, `difficulty_level`, `is_active`),

  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_fill_blank_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_fill_blank_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Fill-in-the-blank questions for vocabulary practice';

CREATE TABLE `edu_question_image` (
  `id` char(36) NOT NULL COMMENT 'Primary Key (UUID)',

  `multiple_choice_question_id` char(36) DEFAULT NULL COMMENT 'Associated Multiple Choice Question ID',
  `free_text_question_id` char(36) DEFAULT NULL COMMENT 'Associated Free Text Question ID',
  `true_false_question_id` char(36) DEFAULT NULL COMMENT 'Associated True/False Question ID',
  `fill_blank_question_id` char(36) DEFAULT NULL COMMENT 'Associated Fill-in-the-Blank Question ID',

  `filename` varchar(60) NOT NULL COMMENT 'Stored filename',
  `original_url` varchar(256) DEFAULT NULL COMMENT 'Original source URL',
  `width` smallint UNSIGNED DEFAULT NULL COMMENT 'Image width in pixels',
  `height` smallint UNSIGNED DEFAULT NULL COMMENT 'Image height in pixels',

  `lang` enum('EN','ZH') NOT NULL DEFAULT 'EN' COMMENT 'Content language',
  `display_order` int NOT NULL DEFAULT 0 COMMENT 'Display order (lower = higher priority)',

  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
  `created_by` char(36) DEFAULT NULL COMMENT 'Created by user ID',
  `updated_by` char(36) DEFAULT NULL COMMENT 'Last updated by user ID',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Active status flag',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_edu_question_image_filename` (`filename`),
  
  -- Indexes
  KEY `idx_edu_question_image_mcq` (`multiple_choice_question_id`, `is_active`, `display_order`),
  KEY `idx_edu_question_image_ftq` (`free_text_question_id`, `is_active`, `display_order`),
  KEY `idx_edu_question_image_tfq` (`true_false_question_id`, `is_active`, `display_order`),
  KEY `idx_edu_question_image_fbq` (`fill_blank_question_id`, `is_active`, `display_order`),
  KEY `idx_edu_question_image_active_lang_order` (`is_active`, `lang`, `display_order`),
  KEY `idx_edu_question_image_created_at` (`created_at`),

  -- Foreign Key Constraints
  CONSTRAINT `fk_edu_question_image_mcq` FOREIGN KEY (`multiple_choice_question_id`) REFERENCES `edu_multiple_choice_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_question_image_ftq` FOREIGN KEY (`free_text_question_id`) REFERENCES `edu_free_text_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_question_image_tfq` FOREIGN KEY (`true_false_question_id`) REFERENCES `edu_true_false_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_question_image_fbq` FOREIGN KEY (`fill_blank_question_id`) REFERENCES `edu_fill_blank_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_question_image_created_by` FOREIGN KEY (`created_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_edu_question_image_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `auth_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Images associated with questions and answers';
