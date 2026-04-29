UPDATE assessment_db.assessments
SET
    title = CASE id
        WHEN '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid THEN 'Quiz 1: Tổng quan môn học'
        WHEN '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid THEN 'Bài tập Lab 1'
        ELSE title
    END,
    updated_at = now()
WHERE id IN (
    '1c9051a1-e968-4e3d-87f5-3c1820e4a951',
    '29cde1ff-4fa2-4d30-9927-b61f83b5b388'
);

UPDATE assessment_db.questions
SET
    content = CASE id
        WHEN 'a1000000-0000-0000-0000-000000000001'::uuid THEN 'Câu 1: LMS là viết tắt của cụm từ nào?'
        WHEN 'a1000000-0000-0000-0000-000000000002'::uuid THEN 'Câu 2: Thành phần nào dùng để theo dõi tiến độ học tập?'
        WHEN 'a1000000-0000-0000-0000-000000000003'::uuid THEN 'Câu 3: Tỷ lệ hoàn thành được tính dựa trên dữ liệu nào?'
        WHEN 'a1000000-0000-0000-0000-000000000004'::uuid THEN 'Câu 4: Khi nào một bài nộp được xem là trễ hạn?'
        WHEN 'a1000000-0000-0000-0000-000000000005'::uuid THEN 'Câu 5: Chỉ số nào giúp phát hiện sinh viên cần can thiệp?'
        ELSE content
    END,
    updated_at = now()
WHERE id IN (
    'a1000000-0000-0000-0000-000000000001',
    'a1000000-0000-0000-0000-000000000002',
    'a1000000-0000-0000-0000-000000000003',
    'a1000000-0000-0000-0000-000000000004',
    'a1000000-0000-0000-0000-000000000005'
);

UPDATE assessment_db.answer_options
SET
    content = CASE id
        WHEN 'a2000000-0000-0000-0001-000000000001'::uuid THEN 'Hệ thống quản lý học tập'
        WHEN 'a2000000-0000-0000-0001-000000000002'::uuid THEN 'Dịch vụ theo dõi bài giảng'
        WHEN 'a2000000-0000-0000-0001-000000000003'::uuid THEN 'Chuẩn ánh xạ học tập'
        WHEN 'a2000000-0000-0000-0001-000000000004'::uuid THEN 'Điểm quản lý bài học'
        WHEN 'a2000000-0000-0000-0002-000000000001'::uuid THEN 'Tiến độ học tập'
        WHEN 'a2000000-0000-0000-0002-000000000002'::uuid THEN 'Sự kiện lịch'
        WHEN 'a2000000-0000-0000-0002-000000000003'::uuid THEN 'Chủ đề diễn đàn'
        WHEN 'a2000000-0000-0000-0002-000000000004'::uuid THEN 'Tệp đính kèm'
        WHEN 'a2000000-0000-0000-0003-000000000001'::uuid THEN 'Thời gian học so với thời lượng ước tính'
        WHEN 'a2000000-0000-0000-0003-000000000002'::uuid THEN 'Số file đã tải lên'
        WHEN 'a2000000-0000-0000-0003-000000000003'::uuid THEN 'Số lần đăng nhập'
        WHEN 'a2000000-0000-0000-0003-000000000004'::uuid THEN 'Số thông báo đã đọc'
        WHEN 'a2000000-0000-0000-0004-000000000001'::uuid THEN 'Thời điểm nộp sau hạn đóng bài'
        WHEN 'a2000000-0000-0000-0004-000000000002'::uuid THEN 'Thời điểm nộp trước thời gian mở bài'
        WHEN 'a2000000-0000-0000-0004-000000000003'::uuid THEN 'Điểm dưới trung bình'
        WHEN 'a2000000-0000-0000-0004-000000000004'::uuid THEN 'Chưa có nhận xét'
        WHEN 'a2000000-0000-0000-0005-000000000001'::uuid THEN 'Sinh viên cần can thiệp'
        WHEN 'a2000000-0000-0000-0005-000000000002'::uuid THEN 'Số chương trong khóa học'
        WHEN 'a2000000-0000-0000-0005-000000000003'::uuid THEN 'Số giảng viên trong khoa'
        WHEN 'a2000000-0000-0000-0005-000000000004'::uuid THEN 'Số file PDF'
        ELSE content
    END,
    updated_at = now()
WHERE id IN (
    'a2000000-0000-0000-0001-000000000001',
    'a2000000-0000-0000-0001-000000000002',
    'a2000000-0000-0000-0001-000000000003',
    'a2000000-0000-0000-0001-000000000004',
    'a2000000-0000-0000-0002-000000000001',
    'a2000000-0000-0000-0002-000000000002',
    'a2000000-0000-0000-0002-000000000003',
    'a2000000-0000-0000-0002-000000000004',
    'a2000000-0000-0000-0003-000000000001',
    'a2000000-0000-0000-0003-000000000002',
    'a2000000-0000-0000-0003-000000000003',
    'a2000000-0000-0000-0003-000000000004',
    'a2000000-0000-0000-0004-000000000001',
    'a2000000-0000-0000-0004-000000000002',
    'a2000000-0000-0000-0004-000000000003',
    'a2000000-0000-0000-0004-000000000004',
    'a2000000-0000-0000-0005-000000000001',
    'a2000000-0000-0000-0005-000000000002',
    'a2000000-0000-0000-0005-000000000003',
    'a2000000-0000-0000-0005-000000000004'
);
