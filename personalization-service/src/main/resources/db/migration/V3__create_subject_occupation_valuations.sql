-- Subject valuation results stored in personalization schema
CREATE TABLE IF NOT EXISTS personalization.subject_occupation_valuations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    subject_id uuid NOT NULL,
    subject_code varchar(50),
    subject_name varchar(255),
    target_occupation_code varchar(10) NOT NULL,
    target_occupation_title varchar(255),
    total_value numeric(18,4) NOT NULL,
    value_density numeric(18,4) NOT NULL,
    details jsonb,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_subject_occupation_val ON personalization.subject_occupation_valuations(subject_id, target_occupation_code);
