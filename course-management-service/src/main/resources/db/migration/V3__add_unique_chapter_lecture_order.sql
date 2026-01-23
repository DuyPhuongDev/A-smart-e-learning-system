ALTER TABLE chapters ADD CONSTRAINT uk_class_section_chapter_order UNIQUE (class_section_id, order_index);

ALTER TABLE lectures ADD CONSTRAINT uk_chapter_lecture_order UNIQUE (chapter_id, order_index);