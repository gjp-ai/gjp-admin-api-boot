-- Use the database
USE gjp_db;

-- ============================================================================
-- Auth Seed Data: Users
-- ============================================================================

-- Super admin user (password: Admin@123)
-- BCrypt hash generated with cost factor 10
INSERT INTO auth_users (id, nickname, username, email, mobile_country_code, mobile_number, password_hash, account_status, password_changed_at, created_by, updated_by)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Super Admin', 'superadmin', 'superadmin@gmail.com', '65', '80000001', '$2a$10$7mPJASDnxHQl5j7MP0SE1euOWn7DkzX95kymsJqeEzi.bHmRLnZSq', 'active', CURRENT_TIMESTAMP, NULL, NULL);

-- Admin user (password: Admin@123)
INSERT INTO auth_users (id, nickname, username, email, mobile_country_code, mobile_number, password_hash, account_status, password_changed_at, created_by, updated_by)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Admin', 'admin', 'admin@gmail.com', '65', '80000002', '$2a$10$7mPJASDnxHQl5j7MP0SE1euOWn7DkzX95kymsJqeEzi.bHmRLnZSq', 'active', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Editor user (password: Admin@123)
INSERT INTO auth_users (id, nickname, username, email, mobile_country_code, mobile_number, password_hash, account_status, password_changed_at, created_by, updated_by)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'Editor', 'editor', 'editor@gmail.com', '65', '80000003', '$2a$10$7mPJASDnxHQl5j7MP0SE1euOWn7DkzX95kymsJqeEzi.bHmRLnZSq', 'active', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Regular user (password: Admin@123)
INSERT INTO auth_users (id, nickname, username, email, mobile_country_code, mobile_number, password_hash, account_status, password_changed_at, created_by, updated_by)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'User', 'user', 'user@gmail.com', '65', '80000004', '$2a$10$7mPJASDnxHQl5j7MP0SE1euOWn7DkzX95kymsJqeEzi.bHmRLnZSq', 'active', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Set super admin's created_by to self (after insert)
UPDATE auth_users SET created_by = 'f47ac10b-58cc-4372-a567-0e02b2c3d479', updated_by = 'f47ac10b-58cc-4372-a567-0e02b2c3d479' WHERE id = 'f47ac10b-58cc-4372-a567-0e02b2c3d479';

-- ============================================================================
-- Auth Seed Data: Roles
-- ============================================================================

INSERT INTO auth_roles (id, code, name, description, parent_role_id, level, is_system_role, sort_order, created_by, updated_by) VALUES
-- Level 0: Top-level roles
('10000000-0000-4000-a000-000000000001', 'SUPER_ADMIN',     'Super Administrator',       'Root-level access with all system privileges including user management and system configuration',   NULL,                                        0, TRUE,  1,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('10000000-0000-4000-a000-000000000002', 'ADMIN',           'System Administrator',       'Full administrative access to content, users, and most system features',                             NULL,                                        0, TRUE,  2,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Level 1: Sub-administrative roles (parent: ADMIN)
('10000000-0000-4000-a000-000000000003', 'CONTENT_MANAGER', 'Content Manager',            'Manages all content categories, publication workflows, and content organization',                   '10000000-0000-4000-a000-000000000002', 1, TRUE,  3,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('10000000-0000-4000-a000-000000000004', 'USER_MANAGER',    'User Manager',               'Manages user accounts, roles, and permissions (except super admin functions)',                       '10000000-0000-4000-a000-000000000002', 1, TRUE,  4,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Level 2: Content roles (parent: CONTENT_MANAGER)
('10000000-0000-4000-a000-000000000005', 'EDITOR',          'Senior Editor',              'Creates, edits, publishes, and manages all content with advanced editorial privileges',              '10000000-0000-4000-a000-000000000003', 2, FALSE, 5,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('10000000-0000-4000-a000-000000000007', 'MODERATOR',       'Content Moderator',          'Reviews, moderates, and manages user-generated content and comments',                               '10000000-0000-4000-a000-000000000003', 2, FALSE, 7,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Level 2: Support role (parent: USER_MANAGER)
('10000000-0000-4000-a000-000000000008', 'SUPPORT_AGENT',   'Customer Support Agent',     'Handles user inquiries, provides technical support, and manages customer relations',                '10000000-0000-4000-a000-000000000004', 2, FALSE, 8,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Level 3: Author role (parent: EDITOR)
('10000000-0000-4000-a000-000000000006', 'AUTHOR',          'Content Author',             'Creates and edits own content, can publish with approval workflow',                                 '10000000-0000-4000-a000-000000000005', 3, FALSE, 6,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Level 0: Special roles
('10000000-0000-4000-a000-000000000009', 'API_CLIENT',      'API Integration Client',     'External system integration access with programmatic API privileges',                               NULL,                                        0, FALSE, 9,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('10000000-0000-4000-a000-000000000010', 'USER',            'Regular User',               'Standard authenticated user with basic reading, commenting, and profile management privileges',     NULL,                                        0, TRUE,  10, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- ============================================================================
-- Auth Seed Data: User-Role Assignments
-- ============================================================================

-- superadmin → SUPER_ADMIN
INSERT INTO auth_user_roles (user_id, role_id, granted_at, created_by, updated_by)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', '10000000-0000-4000-a000-000000000001', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- admin → ADMIN
INSERT INTO auth_user_roles (user_id, role_id, granted_at, created_by, updated_by)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '10000000-0000-4000-a000-000000000002', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- editor → EDITOR
INSERT INTO auth_user_roles (user_id, role_id, granted_at, created_by, updated_by)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', '10000000-0000-4000-a000-000000000005', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- user → USER
INSERT INTO auth_user_roles (user_id, role_id, granted_at, created_by, updated_by)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', '10000000-0000-4000-a000-000000000010', CURRENT_TIMESTAMP, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- ============================================================================
-- Master Seed Data: App Settings
-- ============================================================================

INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
-- AI Application Settings (English)
('20000000-0000-4000-a000-000000000001','app_name','GJP AI','EN','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000002','app_version','1.0.0','EN','AI',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000003','app_description','GAN JIANPING AI Application','EN','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000004','app_company','GAN JIANPING','EN','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000005','app_email','ganjpaien@gmail.com','EN','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000006','app_copyright','Copyright @2026 GANJIANPING','EN','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000007','maintenance_mode','false','EN','AI',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000008','pagination_size','50','EN','AI',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000009','website_tags','AI Chat,AI News,AI Model,AI API,AI App,AI Studio,AI Directory,AI CLI,AI Plugin,Other','EN','AI',0,1,'2026-04-12 23:33:08','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000010','question_tags','Term,Model,Other','EN','AI',0,1,'2026-04-30 05:59:06','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000011','article_tags','Company,AI Guide,AI Tools,AI Models,AI API,AI Infra,AI Company,Hardware,Developer,Other','EN','AI',0,1,'2026-04-18 11:16:49','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000012','image_tags','AI,Celebrity,Other','EN','AI',0,1,'2026-04-18 15:21:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000013','video_tags','Model,Video,Image,Code,App,Plugin,Feature,Tutorial,Other','EN','AI',0,1,'2026-04-23 22:07:16','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000014','audio_tags','AI,Other','EN','AI',0,1,'2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000015','file_tags','AI,Other','EN','AI',0,1,'2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- AI Application Settings (Chinese)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('20000000-0000-4000-b000-000000000001','app_name','甘剑平人工智能','ZH','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001002','app_version','1.0.0','ZH','AI',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001003','app_description','甘剑平人工智能应用','ZH','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001004','app_company','GAN JIANPING','ZH','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001005','app_email','ganjpaien@gmail.com','ZH','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001006','app_copyright','版权 @2026 甘剑平','ZH','AI',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001007','maintenance_mode','false','ZH','AI',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001008','pagination_size','50','ZH','AI',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001009','website_tags','AI对话,AI新闻,AI模型,AI导航,AI编程,AI API,AI绘画生成,AI语音工具,AI视频生成,其它','ZH','AI',0,1,'2026-04-12 23:34:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001010','question_tags','AI知识,AI模型','ZH','AI',0,1,'2026-04-30 06:01:35','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001011','article_tags','公司,产品,模型,开发,其它','ZH','AI',0,1,'2026-04-18 11:18:44','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001012','image_tags','名人,其它','ZH','AI',0,1,'2026-04-18 15:22:10','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001013','video_tags','模型,视频,图片,代码,应用,其它','ZH','AI',0,1,'2026-04-23 22:08:30','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001014','audio_tags','AI,其它','ZH','AI',0,1,'2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-b000-000000001015','file_tags','AI,其它','ZH','AI',0,1,'2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Developer Application Settings (English)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('30000000-0000-4000-a000-000000000001','app_name','GJP Developer','EN','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000002','app_version','1.0.0','EN','Developer',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000003','app_description','GAN JIANPING software development websites, questions, articles, images, audio, video, and files','EN','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000004','app_company','GAN JIANPING','EN','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000005','app_email','ganjpaien@gmail.com','EN','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000006','app_copyright','Copyright @2026 GANJIANPING','EN','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000007','maintenance_mode','false','EN','Developer',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000008','pagination_size','50','EN','Developer',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000009','website_tags','Other','EN','Developer',0,1,'2026-04-12 23:33:08','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000010','question_tags','Other','EN','Developer',0,1,'2026-04-30 05:59:06','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000011','article_tags','Other','EN','Developer',0,1,'2026-04-18 11:16:49','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000012','image_tags','Celebrity,Other','EN','Developer',0,1,'2026-04-18 15:21:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000013','video_tags','Other','EN','Developer',0,1,'2026-04-23 22:07:16','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000014','audio_tags','Other','EN','Developer',0,1,'2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-a000-000000000015','file_tags','Other','EN','Developer',0,1,'2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Developer Application Settings (Chinese)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('30000000-0000-4000-b000-000000000001','app_name','甘剑平软件开发','ZH','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000002','app_version','1.0.0','ZH','Developer',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000003','app_description','甘剑平软件开发相关的网站，博文，图片，问题，音频，视频，文件','ZH','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000004','app_company','GAN JIANPING','ZH','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000005','app_email','ganjpaien@gmail.com','ZH','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000006','app_copyright','版权 @2026 甘剑平','ZH','Developer',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000007','maintenance_mode','false','ZH','Developer',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000008','pagination_size','50','ZH','Developer',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000009','website_tags','其它','ZH','Developer',0,1,'2026-04-12 23:34:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000010','question_tags','其它','ZH','Developer',0,1,'2026-04-30 06:01:35','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000011','article_tags','其它','ZH','Developer',0,1,'2026-04-18 11:18:44','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000012','image_tags','名人,其它','ZH','Developer',0,1,'2026-04-18 15:22:10','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000013','video_tags','其它','ZH','Developer',0,1,'2026-04-23 22:08:30','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000014','audio_tags','其它','ZH','Developer',0,1,'2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('30000000-0000-4000-b000-000000000015','file_tags','其它','ZH','Developer',0,1,'2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Blog Application Settings (English)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('40000000-0000-4000-a000-000000000001','app_name','GJP Blog','EN','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000002','app_version','1.0.0','EN','Blog',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000003','app_description','GAN JIANPING Blog websites, questions, articles, images, audio, video, and files','EN','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000004','app_company','GAN JIANPING','EN','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000005','app_email','ganjpaien@gmail.com','EN','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000006','app_copyright','Copyright @2026 GANJIANPING','EN','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000007','maintenance_mode','false','EN','Blog',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000008','pagination_size','50','EN','Blog',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000009','website_tags','Other','EN','Blog',0,1,'2026-04-12 23:33:08','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000010','question_tags','Other','EN','Blog',0,1,'2026-04-30 05:59:06','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000011','article_tags','Other','EN','Blog',0,1,'2026-04-18 11:16:49','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000012','image_tags','Celebrity,Other','EN','Blog',0,1,'2026-04-18 15:21:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000013','video_tags','Other','EN','Blog',0,1,'2026-04-23 22:07:16','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000014','audio_tags','Other','EN','Blog',0,1,'2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-a000-000000000015','file_tags','Other','EN','Blog',0,1,'2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Blog Application Settings (Chinese)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('40000000-0000-4000-b000-000000000001','app_name','甘剑平博客','ZH','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000002','app_version','1.0.0','ZH','Blog',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000003','app_description','甘剑平博客相关的网站，博文，图片，问题，音频，视频，文件','ZH','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000004','app_company','GAN JIANPING','ZH','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000005','app_email','ganjpaien@gmail.com','ZH','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000006','app_copyright','版权 @2026 甘剑平','ZH','Blog',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000007','maintenance_mode','false','ZH','Blog',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000008','pagination_size','50','ZH','Blog',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000009','website_tags','其它','ZH','Blog',0,1,'2026-04-12 23:34:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000010','question_tags','其它','ZH','Blog',0,1,'2026-04-30 06:01:35','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000011','article_tags','其它','ZH','Blog',0,1,'2026-04-18 11:18:44','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000012','image_tags','名人,其它','ZH','Blog',0,1,'2026-04-18 15:22:10','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000013','video_tags','其它','ZH','Blog',0,1,'2026-04-23 22:08:30','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000014','audio_tags','其它','ZH','Blog',0,1,'2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('40000000-0000-4000-b000-000000000015','file_tags','其它','ZH','Blog',0,1,'2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- English Application Settings (English)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('50000000-0000-4000-a000-000000000001','app_name','GJP English','EN','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000002','app_version','1.0.0','EN','English',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000003','app_description','GAN JIANPING English websites, questions, articles, images, audio, video, and files','EN','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000004','app_company','GAN JIANPING','EN','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000005','app_email','ganjpaien@gmail.com','EN','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000006','app_copyright','Copyright @2026 GANJIANPING','EN','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000007','maintenance_mode','false','EN','English',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000008','pagination_size','50','EN','English',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000009','website_tags','Other','EN','English',0,1,'2026-04-12 23:33:08','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000010','question_tags','Other','EN','English',0,1,'2026-04-30 05:59:06','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000011','article_tags','Other','EN','English',0,1,'2026-04-18 11:16:49','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000012','image_tags','Celebrity,Other','EN','English',0,1,'2026-04-18 15:21:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000013','video_tags','Other','EN','English',0,1,'2026-04-23 22:07:16','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000014','audio_tags','Other','EN','English',0,1,'2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-a000-000000000015','file_tags','Other','EN','English',0,1,'2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- English Application Settings (Chinese)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('50000000-0000-4000-b000-000000000001','app_name','甘剑平英文','ZH','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000002','app_version','1.0.0','ZH','English',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000003','app_description','甘剑平英文相关的网站，博文，图片，问题，音频，视频，文件','ZH','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000004','app_company','GAN JIANPING','ZH','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000005','app_email','ganjpaien@gmail.com','ZH','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000006','app_copyright','版权 @2026 甘剑平','ZH','English',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000007','maintenance_mode','false','ZH','English',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000008','pagination_size','50','ZH','English',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000009','website_tags','其它','ZH','English',0,1,'2026-04-12 23:34:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000010','question_tags','其它','ZH','English',0,1,'2026-04-30 06:01:35','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000011','article_tags','其它','ZH','English',0,1,'2026-04-18 11:18:44','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000012','image_tags','名人,其它','ZH','English',0,1,'2026-04-18 15:22:10','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000013','video_tags','其它','ZH','English',0,1,'2026-04-23 22:08:30','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000014','audio_tags','其它','ZH','English',0,1,'2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('50000000-0000-4000-b000-000000000015','file_tags','其它','ZH','English',0,1,'2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Rubi Application Settings (English)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('90000000-0000-4000-a000-000000000001','app_name','Rubi Study','EN','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000002','app_version','1.0.0','EN','Rubi',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000003','app_description','Rubi Studying websites, questions, articles, images, audio, video, and files','EN','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000004','app_company','GAN JIANPING','EN','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000005','app_email','ganjpaien@gmail.com','EN','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000006','app_copyright','Copyright @2026 GANJIANPING','EN','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000007','maintenance_mode','false','EN','Rubi',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000008','pagination_size','50','EN','Rubi',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:59','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000009','website_tags','Other','EN','Rubi',0,1,'2026-04-12 23:33:08','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000010','question_tags','Other','EN','Rubi',0,1,'2026-04-30 05:59:06','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000011','article_tags','Other','EN','Rubi',0,1,'2026-04-18 11:16:49','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000012','image_tags','Celebrity,Other','EN','Rubi',0,1,'2026-04-18 15:21:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000013','video_tags','Other','EN','Rubi',0,1,'2026-04-23 22:07:16','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000014','audio_tags','Other','EN','Rubi',0,1,'2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:27:38','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-a000-000000000015','file_tags','Other','EN','Rubi',0,1,'2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:45','f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Rubi Application Settings (Chinese)
INSERT INTO master_app_settings (id, name, value, lang, channel, is_system, is_public, created_at, created_by, updated_at, updated_by) VALUES
('90000000-0000-4000-b000-000000000001','app_name','Rubi学习网站','ZH','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000002','app_version','1.0.0','ZH','Rubi',1,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000003','app_description','Rubi学习的相关网站，博文，图片，问题，音频，视频，文件','ZH','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000004','app_company','GAN JIANPING','ZH','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000005','app_email','ganjpaien@gmail.com','ZH','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000006','app_copyright','版权 @2026 甘剑平','ZH','Rubi',0,1,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000007','maintenance_mode','false','ZH','Rubi',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000008','pagination_size','50','ZH','Rubi',1,0,'2026-04-04 19:05:37','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:00','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000009','website_tags','其它','ZH','Rubi',0,1,'2026-04-12 23:34:46','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000010','question_tags','其它','ZH','Rubi',0,1,'2026-04-30 06:01:35','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000011','article_tags','其它','ZH','Rubi',0,1,'2026-04-18 11:18:44','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000012','image_tags','名人,其它','ZH','Rubi',0,1,'2026-04-18 15:22:10','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:03:58','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000013','video_tags','其它','ZH','Rubi',0,1,'2026-04-23 22:08:30','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-24 12:04:01','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000014','audio_tags','其它','ZH','Rubi',0,1,'2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:28:13','f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('90000000-0000-4000-b000-000000000015','file_tags','其它','ZH','Rubi',0,1,'2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479','2026-05-31 02:29:01','f47ac10b-58cc-4372-a567-0e02b2c3d479');
