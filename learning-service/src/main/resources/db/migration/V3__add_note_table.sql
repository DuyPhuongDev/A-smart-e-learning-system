create table learning.lecture_notes (
                               id uuid primary key,

                               student_id uuid not null,
                               lecture_id uuid not null,
                               lecture_type varchar not null,
                               content text not null,

                               content_position integer not null,

                               created_at timestamptz not null default now(),
                               updated_at timestamptz not null default now()
);
