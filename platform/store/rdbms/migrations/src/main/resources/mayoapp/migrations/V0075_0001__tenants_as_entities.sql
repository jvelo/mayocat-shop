INSERT INTO entity (id, slug, type) SELECT id, slug, 'tenant' FROM tenant;

ALTER TABLE tenant DROP COLUMN slug;

ALTER TABLE tenant RENAME COLUMN id TO entity_id;

ALTER TABLE ONLY tenant
ADD CONSTRAINT tenant_entity_fk FOREIGN KEY (entity_id) REFERENCES entity (id);

ALTER TABLE tenant ADD COLUMN configuration TEXT;
ALTER TABLE tenant ADD COLUMN configuration_version SMALLINT;

UPDATE tenant SET configuration = (SELECT data from configuration where id = configuration_id);
UPDATE tenant SET configuration_version = (SELECT version from configuration where id = configuration_id);

ALTER TABLE tenant DROP COLUMN "configuration_id";

DROP TABLE configuration;

-- Make sure there can't be several (slug, type) identical pairs when the tenant if of an entity is null
CREATE UNIQUE INDEX entity_unique_slug_per_type_when_tenant_is_null
ON entity (slug, type) WHERE tenant_id IS NULL;
