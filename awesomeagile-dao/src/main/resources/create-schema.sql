DROP SCHEMA IF EXISTS teams;

CREATE SCHEMA teams;

CREATE TABLE "teams"."user" (
  user_id       SERIAL NOT NULL,
  primary_email TEXT,
  signup_date   TIMESTAMPTZ DEFAULT NOW(),
  status        TEXT,
  display_name  TEXT,
  avatar        TEXT,
  is_visible    BOOLEAN DEFAULT TRUE,
  CONSTRAINT pk_user PRIMARY KEY (user_id)

);

