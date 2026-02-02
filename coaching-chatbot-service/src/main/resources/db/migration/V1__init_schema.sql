-- Tạo schema learning
CREATE SCHEMA IF NOT EXISTS learning AUTHORIZATION lms_user;

-- 1. Bảng quản lý trạng thái kiến thức của bài giảng (1-1 với lectures)
-- Bảng này đóng vai trò là "Cổng kiểm soát". Nếu bài giảng có trong bảng này với status = 'COMPLETED', nghĩa là nó đã sẵn sàng trong Qdrant.
CREATE TABLE learning.lecture_knowledge (
    lecture_knowledge_id uuid NOT NULL, -- Dùng chính ID của lecture làm Primary Key để đảm bảo quan hệ 1-1
    sync_status varchar DEFAULT 'PENDING' NOT NULL,
    -- Các trạng thái:
    -- 'PENDING': Mới tạo, chưa xử lý.
    -- 'PROCESSING': Đang chạy job cắt và embedding.
    -- 'COMPLETED': Đã nạp xong vào Qdrant.
    -- 'FAILED': Gặp lỗi (file lỗi, api lỗi).
    -- 'OUTDATED': Giáo viên mới cập nhật bài giảng gốc, cần chạy lại.
    last_synced_at timestamptz NULL, -- Thời điểm hoàn tất nạp vector gần nhất
    error_message text NULL, -- Lưu log lỗi nếu sync_status = 'FAILED'
    embedding_model varchar NULL, -- Lưu tên model dùng để vector hóa (vd: 'text-embedding-3-small')
    content_type varchar NULL, -- Loại nội dung: VIDEO_YOUTUBE, VIDEO_S3, DOCUMENT_PDF, DOCUMENT_DOCX, TEXT_CONTENT, etc.
    total_chunks int4 DEFAULT 0 NOT NULL, -- Tổng số chunks đã được tạo cho bài giảng này
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT lecture_knowledge_pkey PRIMARY KEY (lecture_knowledge_id)
);

-- 2. Bảng lưu các mảnh kiến thức chi tiết (Chunks)
-- Bảng này lưu text đã cắt nhỏ và vị trí chính xác của nó trong bài giảng gốc.
CREATE TABLE learning.lecture_knowledge_chunks (
    lecture_knowledge_chunks_id uuid NOT NULL,
    lecture_knowledge_id uuid NOT NULL, -- FK trỏ về lecture_knowledge (hoặc lectures đều được vì là 1-1)

    chunk_index int4 NOT NULL, -- Thứ tự của chunk trong bài giảng (0, 1, 2, ...)
    chunk_content text NOT NULL, -- Nội dung text dùng để search (Context)

    qdrant_point_id uuid NOT NULL,
    -- ID của vector trong Qdrant. Lưu lại để sau này nếu xóa bài giảng thì dùng ID này xóa vector tương ứng.

    -- CÁC TRƯỜNG ĐỊNH VỊ (Location Tracking)
    -- Dành cho Video (Transcript)
    start_time_seconds int4 NULL, -- Thời điểm bắt đầu câu nói (ví dụ: giây thứ 120)
    end_time_seconds int4 NULL,   -- Thời điểm kết thúc (ví dụ: giây thứ 135)

    -- Dành cho Tài liệu (PDF/Slide/Doc)
    page_number int4 NULL,        -- Trang số mấy (ví dụ: trang 5)

    token_count int4 NULL, -- Số lượng token của chunk (để tối ưu context window khi gửi cho GPT)

    created_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT lecture_knowledge_chunks_pkey PRIMARY KEY (lecture_knowledge_chunks_id),
    CONSTRAINT lecture_knowledge_chunks_lecture_id_fkey FOREIGN KEY (lecture_knowledge_id) REFERENCES learning.lecture_knowledge(lecture_knowledge_id) ON DELETE CASCADE
);

-- Index để truy vấn nhanh các chunk của 1 bài giảng
CREATE INDEX idx_lecture_chunks_lecture_id ON learning.lecture_knowledge_chunks(lecture_knowledge_id);

-- Index để sắp xếp chunks theo thứ tự
CREATE INDEX idx_lecture_chunks_order ON learning.lecture_knowledge_chunks(lecture_knowledge_id, chunk_index);

