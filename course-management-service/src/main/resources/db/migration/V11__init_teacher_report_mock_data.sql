-- Add estimated lesson durations for the teacher class report demo.
-- Target class: MT1007_HK251_L01.

UPDATE course_management.lectures
SET
    title = CASE id
        WHEN '45d2b312-2849-4aa4-9ccc-ff881966feb9' THEN 'Gioi thieu mon hoc'
        WHEN '3250126c-8341-4080-b964-63c5b0b18820' THEN 'Lab 1: Cong cu va moi truong hoc tap'
        WHEN '19480479-4a40-40d4-b843-2ef53beaf24c' THEN 'Video: Tong quan noi dung mon hoc'
        WHEN '74e22a79-6b77-413f-9053-f6f79566226b' THEN 'On tap: Cau truc bai hoc'
        ELSE title
    END,
    estimate_time_spent = CASE id
        WHEN '45d2b312-2849-4aa4-9ccc-ff881966feb9' THEN 45
        WHEN '3250126c-8341-4080-b964-63c5b0b18820' THEN 60
        WHEN '19480479-4a40-40d4-b843-2ef53beaf24c' THEN 50
        WHEN 'b77e943d-ac1b-4e01-b614-53c501344569' THEN 35
        WHEN '9c1e8ddc-00f9-4393-90b8-5400e63c3b36' THEN 45
        WHEN 'febe6123-a925-44cf-a109-aefc7cf61420' THEN 90
        WHEN '74e22a79-6b77-413f-9053-f6f79566226b' THEN 30
        WHEN '5655cc4d-1de7-4263-9950-dfa39500d821' THEN 40
        ELSE estimate_time_spent
    END,
    completion_rate = CASE id
        WHEN '45d2b312-2849-4aa4-9ccc-ff881966feb9' THEN 0.75
        WHEN '3250126c-8341-4080-b964-63c5b0b18820' THEN 0.61
        WHEN '19480479-4a40-40d4-b843-2ef53beaf24c' THEN 0.64
        WHEN 'b77e943d-ac1b-4e01-b614-53c501344569' THEN 0.68
        WHEN '9c1e8ddc-00f9-4393-90b8-5400e63c3b36' THEN 0.58
        WHEN 'febe6123-a925-44cf-a109-aefc7cf61420' THEN 0.60
        WHEN '74e22a79-6b77-413f-9053-f6f79566226b' THEN 0.72
        WHEN '5655cc4d-1de7-4263-9950-dfa39500d821' THEN 0.65
        ELSE completion_rate
    END,
    view_count = CASE id
        WHEN '45d2b312-2849-4aa4-9ccc-ff881966feb9' THEN 18
        WHEN '3250126c-8341-4080-b964-63c5b0b18820' THEN 16
        WHEN '19480479-4a40-40d4-b843-2ef53beaf24c' THEN 15
        WHEN 'b77e943d-ac1b-4e01-b614-53c501344569' THEN 14
        WHEN '9c1e8ddc-00f9-4393-90b8-5400e63c3b36' THEN 13
        WHEN 'febe6123-a925-44cf-a109-aefc7cf61420' THEN 12
        WHEN '74e22a79-6b77-413f-9053-f6f79566226b' THEN 15
        WHEN '5655cc4d-1de7-4263-9950-dfa39500d821' THEN 14
        ELSE view_count
    END,
    updated_at = now()
WHERE id IN (
    '45d2b312-2849-4aa4-9ccc-ff881966feb9',
    '3250126c-8341-4080-b964-63c5b0b18820',
    '19480479-4a40-40d4-b843-2ef53beaf24c',
    'b77e943d-ac1b-4e01-b614-53c501344569',
    '9c1e8ddc-00f9-4393-90b8-5400e63c3b36',
    'febe6123-a925-44cf-a109-aefc7cf61420',
    '74e22a79-6b77-413f-9053-f6f79566226b',
    '5655cc4d-1de7-4263-9950-dfa39500d821'
);
