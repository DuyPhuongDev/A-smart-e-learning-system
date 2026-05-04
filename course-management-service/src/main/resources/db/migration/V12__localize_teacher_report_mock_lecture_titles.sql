UPDATE course_management.lectures
SET
    title = CASE id
        WHEN '45d2b312-2849-4aa4-9ccc-ff881966feb9' THEN 'Giới thiệu môn học'
        WHEN '3250126c-8341-4080-b964-63c5b0b18820' THEN 'Lab 1: Công cụ và môi trường học tập'
        WHEN '19480479-4a40-40d4-b843-2ef53beaf24c' THEN 'Video: Tổng quan nội dung môn học'
        WHEN '74e22a79-6b77-413f-9053-f6f79566226b' THEN 'Ôn tập: Cấu trúc bài học'
        ELSE title
    END,
    updated_at = now()
WHERE id IN (
    '45d2b312-2849-4aa4-9ccc-ff881966feb9',
    '3250126c-8341-4080-b964-63c5b0b18820',
    '19480479-4a40-40d4-b843-2ef53beaf24c',
    '74e22a79-6b77-413f-9053-f6f79566226b'
);
