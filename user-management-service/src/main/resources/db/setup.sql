-- =====================================================
-- Script tạo user Admin hoàn chỉnh
-- =====================================================

-- 1. Generate UUID
DO $$
DECLARE
    v_user_id UUID := gen_random_uuid();
    v_role_id UUID;
BEGIN
    -- 2. Tạo hoặc lấy role ADMIN
    INSERT INTO user_management.roles (id, name, description, is_active)
    VALUES (gen_random_uuid(), 'ADMIN', 'Administrator role', true)
    ON CONFLICT (name) DO NOTHING;
    
    SELECT id INTO v_role_id FROM user_management.roles WHERE name = 'ADMIN';
    
    -- 3. Tạo credentials trong authentication schema
    INSERT INTO authentication.user_credentials (user_id, email, password_hash)
    VALUES (
        v_user_id,
        'admin@hcmut.edu.vn',
        '$2a$12$.SQtPKQBVUgfROS3FQKDxOWRs7//2bAdZb2AHEcv/prxm7o6wv6n2'  -- password: admin123
    );
    
    -- 4. Tạo user profile trong user_management schema
    INSERT INTO user_management.users (id, email, first_name, last_name, role_id)
    VALUES (
        v_user_id,
        'admin@hcmut.edu.vn',
        'System',
        'Administrator',
        v_role_id
    );
    
    -- 5. Tạo admin profile
    INSERT INTO user_management.admins (user_id, admin_code)
    VALUES (
        v_user_id,
        'ADM001'
    );
    
    RAISE NOTICE 'Admin user created with ID: %', v_user_id;
END $$;