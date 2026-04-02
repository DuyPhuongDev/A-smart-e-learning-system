-- V7: Create goal_validation_results table for storing learning goal validation results

CREATE TABLE IF NOT EXISTS personalization.goal_validation_results (
    validation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learning_goal_id UUID NOT NULL REFERENCES personalization.learning_goals(learning_goal_id) ON DELETE CASCADE,
    student_id UUID NOT NULL,

    -- Validation outcome
    feasibility_level VARCHAR(20) NOT NULL CHECK (feasibility_level IN ('GOOD', 'MEDIUM', 'WEAK')),
    probability_score NUMERIC(5, 4), -- 0.0000 to 1.0000

    -- Step 1 aggregate metrics and checks
    metrics JSONB,
    preliminary_checks JSONB,

    -- Step 2 results (quantitative analysis)
    probability_analysis JSONB,

    -- Step 3 results (recommendations)
    recommendations JSONB,
    warnings JSONB,

    -- Metadata
    validation_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create indexes for efficient querying
CREATE INDEX idx_goal_validation_goal_id ON personalization.goal_validation_results(learning_goal_id);
CREATE INDEX idx_goal_validation_student_id ON personalization.goal_validation_results(student_id);
CREATE INDEX idx_goal_validation_timestamp ON personalization.goal_validation_results(validation_timestamp DESC);
CREATE INDEX idx_goal_validation_feasibility ON personalization.goal_validation_results(feasibility_level);

-- Add comment for documentation
COMMENT ON TABLE personalization.goal_validation_results IS 'Stores validation results for student learning goals, including feasibility assessment and recommendations';
COMMENT ON COLUMN personalization.goal_validation_results.feasibility_level IS 'Overall feasibility classification: GOOD, MEDIUM, or WEAK';
COMMENT ON COLUMN personalization.goal_validation_results.probability_score IS 'Probability of achieving the goal (0.0 to 1.0)';
COMMENT ON COLUMN personalization.goal_validation_results.metrics IS 'Aggregate feasibility metrics generated during preliminary checks';
COMMENT ON COLUMN personalization.goal_validation_results.preliminary_checks IS 'List of preliminary check results (credit-time, GPA, prerequisite chain, graduation requirements)';
COMMENT ON COLUMN personalization.goal_validation_results.probability_analysis IS 'Detailed probability analysis (historical or predictive)';
COMMENT ON COLUMN personalization.goal_validation_results.recommendations IS 'Personalized recommendations for goal adjustment';
COMMENT ON COLUMN personalization.goal_validation_results.warnings IS 'Warnings about potential risks or issues';
