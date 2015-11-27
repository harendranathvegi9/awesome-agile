---
-- ================================================================================================
-- Awesome Agile
-- %%
-- Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ------------------------------------------------------------------------------------------------
---
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

