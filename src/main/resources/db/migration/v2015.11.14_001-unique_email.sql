ALTER TABLE teams.user
    ADD CONSTRAINT user_email_key UNIQUE (primary_email);
