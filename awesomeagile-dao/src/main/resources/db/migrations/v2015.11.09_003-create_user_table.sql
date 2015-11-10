CREATE TABLE "teams"."user" (
  user_id       BIGSERIAL PRIMARY KEY,
  primary_email TEXT,
  signup_date   TIMESTAMPTZ DEFAULT NOW(),
  status        TEXT,
  display_name  TEXT,
  avatar        TEXT,
  is_visible    BOOLEAN DEFAULT TRUE
);

