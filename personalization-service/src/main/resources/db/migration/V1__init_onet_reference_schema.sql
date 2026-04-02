-- =========================================================
-- O*NET REFERENCE SCHEMA INITIALIZATION
-- =========================================================
CREATE SCHEMA IF NOT EXISTS personalization AUTHORIZATION lms_user;
CREATE EXTENSION IF NOT EXISTS vector;
ALTER EXTENSION vector SET SCHEMA public;

CREATE TABLE IF NOT EXISTS personalization.occupation_data (
    occupation_data_id uuid NOT NULL DEFAULT gen_random_uuid(),
    onetsoc_code varchar(10) NOT NULL,
    title varchar(150) NOT NULL,
    title_vn varchar(150) NOT NULL,
    description varchar(1000) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT occupation_data_pkey PRIMARY KEY (occupation_data_id),
    CONSTRAINT occupation_data_onetsoc_code_uk UNIQUE (onetsoc_code)
);

CREATE TABLE IF NOT EXISTS personalization.iwa_reference (
    iwa_reference_id uuid NOT NULL DEFAULT gen_random_uuid(),
    iwa_id varchar(20) NOT NULL,
    element_id varchar(20) NOT NULL,
    element_name varchar(150) NOT NULL,
    element_description varchar(1500) NOT NULL,
    iwa_title varchar(150) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT iwa_reference_pkey PRIMARY KEY (iwa_reference_id),
    CONSTRAINT iwa_reference_iwa_id_uk UNIQUE (iwa_id)
);

CREATE TABLE IF NOT EXISTS personalization.dwa_reference (
    dwa_reference_id uuid NOT NULL DEFAULT gen_random_uuid(),
    dwa_id varchar(20) NOT NULL,
    iwa_id varchar(20) NOT NULL,
    element_id varchar(20) NOT NULL,
    element_name varchar(150) NOT NULL,
    element_description varchar(1500) NOT NULL,
    dwa_title varchar(150) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dwa_reference_pkey PRIMARY KEY (dwa_reference_id),
    CONSTRAINT dwa_reference_dwa_id_uk UNIQUE (dwa_id),
    CONSTRAINT fk_dwa_reference_iwa FOREIGN KEY (iwa_id)
        REFERENCES personalization.iwa_reference(iwa_id)
);

CREATE TABLE IF NOT EXISTS personalization.task_statements (
    task_statements_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id numeric(8,0) NOT NULL,
    onetsoc_code varchar(10) NOT NULL,
    task varchar(1000) NOT NULL,
    task_type varchar(12),
    incumbents_responding numeric(4,0),
    date_updated date NOT NULL,
    domain_source varchar(30) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT task_statements_task_id_uk UNIQUE (task_id),
    CONSTRAINT fk_task_statements_occupation FOREIGN KEY (onetsoc_code)
        REFERENCES personalization.occupation_data(onetsoc_code)
);

CREATE TABLE IF NOT EXISTS personalization.tasks_to_dwas (
    tasks_to_dwas_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    onetsoc_code varchar(10) NOT NULL,
    task_id numeric(8,0) NOT NULL,
    dwa_id varchar(20) NOT NULL,
    date_updated date NOT NULL,
    domain_source varchar(30) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_tasks_to_dwas_occupation FOREIGN KEY (onetsoc_code)
        REFERENCES personalization.occupation_data(onetsoc_code),
    CONSTRAINT fk_tasks_to_dwas_task FOREIGN KEY (task_id)
        REFERENCES personalization.task_statements(task_id),
    CONSTRAINT fk_tasks_to_dwas_dwa FOREIGN KEY (dwa_id)
        REFERENCES personalization.dwa_reference(dwa_id)
);

CREATE TABLE IF NOT EXISTS personalization.onet_flattened_requirements (
    onet_flattened_requirements_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    onetsoc_code varchar(10) NOT NULL,
    element_type varchar(20) NOT NULL,
    original_id varchar(50),
    content_text text,
    importance_score numeric(5,2),
    created_at timestamptz DEFAULT now(),
    CONSTRAINT fk_onet_flattened_requirements_occupation FOREIGN KEY (onetsoc_code)
        REFERENCES personalization.occupation_data(onetsoc_code)
);


-- Sidecar table for requirement embeddings
CREATE TABLE IF NOT EXISTS personalization.onet_flattened_requirement_embeddings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    requirement_id uuid NOT NULL,
    occupation_code varchar(20),
    embedding vector(768)
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_schema = 'personalization'
          AND table_name = 'onet_flattened_requirement_embeddings'
          AND constraint_name = 'fk_requirement'
    ) THEN
        ALTER TABLE personalization.onet_flattened_requirement_embeddings
            ADD CONSTRAINT fk_requirement
                FOREIGN KEY (requirement_id)
                REFERENCES personalization.onet_flattened_requirements(onet_flattened_requirements_id)
                ON DELETE CASCADE;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_requirement_embedding_requirement_id
    ON personalization.onet_flattened_requirement_embeddings(requirement_id);

CREATE INDEX IF NOT EXISTS idx_requirement_embedding_hnsw
    ON personalization.onet_flattened_requirement_embeddings
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

COMMENT ON TABLE personalization.occupation_data IS 'Master occupation list keyed by O*NET SOC code.';

COMMENT ON TABLE personalization.iwa_reference IS 'Intermediate Work Activities with embedded content model information.';
COMMENT ON COLUMN personalization.iwa_reference.iwa_id IS 'Unique IWA identifier.';
COMMENT ON COLUMN personalization.iwa_reference.element_id IS 'Content model element identifier related to this IWA.';
COMMENT ON COLUMN personalization.iwa_reference.element_name IS 'Content model element name related to this IWA.';
COMMENT ON COLUMN personalization.iwa_reference.element_description IS 'Content model element description related to this IWA.';
COMMENT ON COLUMN personalization.iwa_reference.iwa_title IS 'Title/description of the intermediate work activity.';

COMMENT ON TABLE personalization.dwa_reference IS 'Detailed Work Activities with embedded content model information.';
COMMENT ON COLUMN personalization.dwa_reference.dwa_id IS 'Unique DWA identifier.';
COMMENT ON COLUMN personalization.dwa_reference.iwa_id IS 'Parent IWA identifier for this DWA.';
COMMENT ON COLUMN personalization.dwa_reference.element_id IS 'Content model element identifier related to this DWA.';
COMMENT ON COLUMN personalization.dwa_reference.element_name IS 'Content model element name related to this DWA.';
COMMENT ON COLUMN personalization.dwa_reference.element_description IS 'Content model element description related to this DWA.';
COMMENT ON COLUMN personalization.dwa_reference.dwa_title IS 'Title/description of the detailed work activity.';

COMMENT ON TABLE personalization.task_statements IS 'Task statements associated with occupations.';
COMMENT ON TABLE personalization.tasks_to_dwas IS 'Bridge table mapping tasks to detailed work activities.';
COMMENT ON TABLE personalization.onet_flattened_requirements IS 'Denormalized requirements table for unified retrieval across skills, tasks, DWA, and IWA.';

CREATE INDEX IF NOT EXISTS idx_iwa_reference_element_id
    ON personalization.iwa_reference(element_id);

CREATE INDEX IF NOT EXISTS idx_dwa_reference_element_id
    ON personalization.dwa_reference(element_id);

CREATE INDEX IF NOT EXISTS idx_dwa_reference_iwa_id
    ON personalization.dwa_reference(iwa_id);

CREATE INDEX IF NOT EXISTS idx_task_statements_onetsoc_code
    ON personalization.task_statements(onetsoc_code);

CREATE INDEX IF NOT EXISTS idx_tasks_to_dwas_onetsoc_code
    ON personalization.tasks_to_dwas(onetsoc_code);

CREATE INDEX IF NOT EXISTS idx_tasks_to_dwas_task_id
    ON personalization.tasks_to_dwas(task_id);

CREATE INDEX IF NOT EXISTS idx_tasks_to_dwas_dwa_id
    ON personalization.tasks_to_dwas(dwa_id);

CREATE INDEX IF NOT EXISTS idx_onet_flat_soc
    ON personalization.onet_flattened_requirements(onetsoc_code);

CREATE INDEX IF NOT EXISTS idx_onet_flat_type
    ON personalization.onet_flattened_requirements(element_type);
