alter table essay_submissions add column if not exists submission_files JSONB default '[]'::jsonb;

alter table essay_questions add column if not exists instruction_files jsonb default '[]'::jsonb;

alter table essay_submissions drop column if exists answer_file_url;

alter table essay_submissions drop column if exists file_format;

alter table essay_submissions drop column if exists word_count;

alter table essay_submissions drop column if exists file_format;

