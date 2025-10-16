-- Insert predefined system roles
INSERT INTO roles (id, name, description, type, is_active, created_at, updated_at) VALUES
    (RANDOM_UUID(), 'STUDENT', 'Student role with basic permissions', 'SYSTEM_PREDEFINED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'TEACHER', 'Teacher role with instructor permissions', 'SYSTEM_PREDEFINED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'ADMIN', 'Administrator role with full system access', 'SYSTEM_PREDEFINED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

