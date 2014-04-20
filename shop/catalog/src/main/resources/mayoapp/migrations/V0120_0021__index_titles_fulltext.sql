---
--- Add full text index and function so that we can search products by title
---

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE OR REPLACE FUNCTION m_unaccent(text)
  RETURNS text AS
$func$
SELECT unaccent('unaccent', $1)
$func$  LANGUAGE sql IMMUTABLE SET search_path = public, pg_temp;

CREATE INDEX product_title_fulltext_index ON product
USING GIN (lower(m_unaccent(product.title)) gin_trgm_ops);
