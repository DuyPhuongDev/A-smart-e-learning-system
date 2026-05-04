-- Adjust teacher report mock study sessions to create clearer lecture-view variation.

UPDATE learning.study_times
SET
    duration_seconds = CASE id
        WHEN 'f0000000-0000-0000-0000-000000000023'::uuid THEN 0
        WHEN 'f0000000-0000-0000-0000-000000000028'::uuid THEN 0
        WHEN 'f0000000-0000-0000-0000-000000000029'::uuid THEN 0
        WHEN 'f0000000-0000-0000-0000-000000000031'::uuid THEN 0
        WHEN 'f0000000-0000-0000-0000-000000000032'::uuid THEN 0
        ELSE duration_seconds
    END,
    ended_at = started_at,
    updated_at = now()
WHERE id IN (
    'f0000000-0000-0000-0000-000000000023',
    'f0000000-0000-0000-0000-000000000028',
    'f0000000-0000-0000-0000-000000000029',
    'f0000000-0000-0000-0000-000000000031',
    'f0000000-0000-0000-0000-000000000032'
);
