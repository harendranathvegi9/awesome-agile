DROP SCHEMA IF EXISTS teams;

CREATE SCHEMA teams;

CREATE TABLE "teams"."user" (
  id                 BIGSERIAL NOT NULL,
  primary_email      TEXT,
  created_date       TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  last_modified_date TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  status             TEXT,
  display_name       TEXT,
  avatar             TEXT,
  is_visible         BOOLEAN     DEFAULT TRUE,
  CONSTRAINT pk_user PRIMARY KEY (id)

);

