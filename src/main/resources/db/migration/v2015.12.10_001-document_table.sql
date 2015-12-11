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
CREATE TABLE "teams"."document" (
  id BIGSERIAL NOT NULL,
  url TEXT NOT NULL,
  document_type TEXT NOT NULL,
  user_id BIGINT NOT NULL REFERENCES "teams"."user" (id),
  created_date TIMESTAMPTZ NOT NULL,
  last_modified_date TIMESTAMPTZ NOT NULL,
  UNIQUE (url)
);