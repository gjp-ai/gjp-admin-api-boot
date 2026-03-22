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

INSERT INTO master_app_settings (id, name, value, lang, is_system, is_public, created_by, updated_by) VALUES
-- Application Settings (English)
('20000000-0000-4000-a000-000000000001', 'app_name',          'GJP System',              'EN', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000002', 'app_version',       '1.0.0',                   'EN', TRUE,  TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000003', 'app_description',   'GJP Admin System',        'EN', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000004', 'app_company',       'GJP AI',                  'EN', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000005', 'app_email',         'support@gmail.com',         'EN', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000006', 'app_copyright',     'Copyright 2025 GJP AI',   'EN', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000007', 'maintenance_mode',  'false',                   'EN', TRUE,  FALSE, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000000008', 'pagination_size',   '20',                      'EN', TRUE,  FALSE, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
-- Application Settings (Chinese)
('20000000-0000-4000-a000-000000001001', 'app_name',          'GJP 系统',                'ZH', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001002', 'app_version',       '1.0.0',                   'ZH', TRUE,  TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001003', 'app_description',   'GJP 管理系统',            'ZH', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001004', 'app_company',       'GJP AI',                  'ZH', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001005', 'app_email',         'support@gmail.com',         'ZH', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001006', 'app_copyright',     'Copyright 2025 GJP AI',   'ZH', FALSE, TRUE,  'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001007', 'maintenance_mode',  'false',                   'ZH', TRUE,  FALSE, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('20000000-0000-4000-a000-000000001008', 'pagination_size',   '20',                      'ZH', TRUE,  FALSE, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');
