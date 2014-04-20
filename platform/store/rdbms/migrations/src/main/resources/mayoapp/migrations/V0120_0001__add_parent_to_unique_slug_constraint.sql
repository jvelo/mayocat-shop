---
--- Drop former constraints
---

ALTER TABLE entity DROP CONSTRAINT entity_unique_slug_per_type_per_tenant;
DROP INDEX entity_unique_slug_per_type_when_tenant_is_null;

---
--- Add new ones
---

ALTER TABLE ONLY entity
  ADD CONSTRAINT entity_unique_slug_per_type_per_parent_per_tenant UNIQUE (slug, type, tenant_id, parent_id);

CREATE UNIQUE INDEX entity_unique_slug_per_type_per_parent_when_tenant_is_null
  ON entity (slug, type, parent_id) WHERE tenant_id IS NULL;

