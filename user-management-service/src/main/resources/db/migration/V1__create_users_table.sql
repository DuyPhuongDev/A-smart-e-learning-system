CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    avatar VARCHAR(500),
    phone VARCHAR(20),
    address VARCHAR(500),
    department VARCHAR(100),
    student_id VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_student_id ON users(student_id);

