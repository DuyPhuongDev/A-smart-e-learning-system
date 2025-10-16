CREATE TABLE endpoint_registry (
    id UUID PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_endpoint UNIQUE (endpoint, http_method, service_name)
);

CREATE INDEX idx_service_name ON endpoint_registry(service_name);
CREATE INDEX idx_is_active ON endpoint_registry(is_active);

