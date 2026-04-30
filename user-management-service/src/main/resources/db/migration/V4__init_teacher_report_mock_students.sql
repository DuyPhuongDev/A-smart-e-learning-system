-- Mock students for the teacher class report demo.
-- Target class enrollment is seeded in learning-service.

INSERT INTO user_management.users (
    id,
    email,
    avatar_url,
    first_name,
    last_name,
    phone,
    last_login,
    specialization_id,
    role_id,
    created_at,
    updated_at
) VALUES
    (
        '9d46f868-7b0c-45cf-8d3a-a3447019377b',
        'student2@hcmut.edu.vn',
        NULL,
        'Binh',
        'Nguyen Van',
        '0901234562',
        NULL,
        '1ba8a25e-70cd-430d-8dbe-92d12f178613',
        'e5909d1e-492d-4419-9766-ce69821e28ec',
        now(),
        now()
    ),
    (
        '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5',
        'student3@hcmut.edu.vn',
        NULL,
        'Chi',
        'Tran Minh',
        '0901234563',
        NULL,
        '1ba8a25e-70cd-430d-8dbe-92d12f178613',
        'e5909d1e-492d-4419-9766-ce69821e28ec',
        now(),
        now()
    ),
    (
        '610c2b86-0f1d-4c34-a517-aea2b92ce27d',
        'student4@hcmut.edu.vn',
        NULL,
        'Dung',
        'Le Quang',
        '0901234564',
        NULL,
        '1ba8a25e-70cd-430d-8dbe-92d12f178613',
        'e5909d1e-492d-4419-9766-ce69821e28ec',
        now(),
        now()
    ),
    (
        'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8',
        'student5@hcmut.edu.vn',
        NULL,
        'Ha',
        'Pham Thu',
        '0901234565',
        NULL,
        '1ba8a25e-70cd-430d-8dbe-92d12f178613',
        'e5909d1e-492d-4419-9766-ce69821e28ec',
        now(),
        now()
    )
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_management.students (
    user_id,
    student_code,
    intake_year_id,
    department_id
) VALUES
    (
        '9d46f868-7b0c-45cf-8d3a-a3447019377b',
        '2110002',
        '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8',
        'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371'
    ),
    (
        '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5',
        '2110003',
        '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8',
        'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371'
    ),
    (
        '610c2b86-0f1d-4c34-a517-aea2b92ce27d',
        '2110004',
        '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8',
        'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371'
    ),
    (
        'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8',
        '2110005',
        '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8',
        'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371'
    )
ON CONFLICT (user_id) DO NOTHING;
