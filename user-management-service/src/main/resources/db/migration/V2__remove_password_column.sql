-- =====================================================
-- MIGRATION V2: Remove password column from users table
-- =====================================================
-- Password management is now handled by authentication-service
-- =====================================================

SET search_path TO user_management;

-- Remove password column from users table
ALTER TABLE users DROP COLUMN IF EXISTS password;

-- Note: last_login is kept in users table for display purposes
-- It can be synced from authentication-service if needed

