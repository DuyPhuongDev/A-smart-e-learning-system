UPDATE user_management.users
SET
    first_name = CASE id
        WHEN '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid THEN 'Bình'
        WHEN '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid THEN 'Chi'
        WHEN '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid THEN 'Dũng'
        WHEN 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid THEN 'Hà'
        ELSE first_name
    END,
    last_name = CASE id
        WHEN '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid THEN 'Nguyễn Văn'
        WHEN '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid THEN 'Trần Minh'
        WHEN '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid THEN 'Lê Quang'
        WHEN 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid THEN 'Phạm Thu'
        ELSE last_name
    END,
    updated_at = now()
WHERE id IN (
    '9d46f868-7b0c-45cf-8d3a-a3447019377b',
    '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5',
    '610c2b86-0f1d-4c34-a517-aea2b92ce27d',
    'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'
);
