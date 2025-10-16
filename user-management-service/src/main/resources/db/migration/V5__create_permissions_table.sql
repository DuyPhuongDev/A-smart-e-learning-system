CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    role_id UUID NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    is_allowed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_permissions_role_id ON permissions(role_id);
CREATE INDEX idx_permissions_endpoint ON permissions(endpoint, http_method);

