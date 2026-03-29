ALTER TABLE question_banks add constraint unq_owner_name unique (name, owner_id);

