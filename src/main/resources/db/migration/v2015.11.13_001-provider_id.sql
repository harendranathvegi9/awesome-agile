ALTER TABLE teams.user
    ADD COLUMN auth_provider_id TEXT,
    ADD COLUMN auth_provider_user_id TEXT,
    ADD CONSTRAINT user_provider_key UNIQUE (auth_provider_id, auth_provider_user_id);
