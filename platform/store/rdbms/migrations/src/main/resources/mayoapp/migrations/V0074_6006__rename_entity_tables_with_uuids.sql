ALTER TABLE translation_small_with_uuid RENAME TO translation_small;
ALTER TABLE translation_small DROP COLUMN id, DROP COLUMN translation_id;
ALTER TABLE translation_small RENAME COLUMN translation_id_with_uuid TO translation_id;
ALTER TABLE translation_small ALTER COLUMN translation_id SET NOT NULL;

ALTER TABLE translation_medium_with_uuid RENAME TO translation_medium;
ALTER TABLE translation_medium DROP COLUMN id, DROP COLUMN translation_id;
ALTER TABLE translation_medium RENAME COLUMN translation_id_with_uuid TO translation_id;
ALTER TABLE translation_medium ALTER COLUMN translation_id SET NOT NULL;

ALTER TABLE translation_with_uuid RENAME TO translation;
ALTER TABLE translation DROP COLUMN id, DROP COLUMN entity_id;
ALTER TABLE translation RENAME COLUMN uuid TO id;
ALTER TABLE translation RENAME COLUMN entity_with_uuid_id TO entity_id;
ALTER TABLE translation ALTER COLUMN id SET NOT NULL;

ALTER TABLE entity_with_uuid RENAME TO entity;
ALTER TABLE entity DROP COLUMN id, DROP COLUMN tenant_id, DROP COLUMN parent_id;
ALTER TABLE entity RENAME COLUMN uuid TO id;
ALTER TABLE entity RENAME COLUMN tenant_with_uuid_id TO tenant_id;
ALTER TABLE entity RENAME COLUMN parent_with_uuid_id TO parent_id;
ALTER TABLE entity ALTER COLUMN id SET NOT NULL;

ALTER TABLE tenant_with_uuid RENAME TO tenant;
ALTER TABLE tenant DROP COLUMN id, DROP COLUMN configuration_id;
ALTER TABLE tenant RENAME COLUMN uuid TO id;
ALTER TABLE tenant RENAME COLUMN configuration_with_uuid_id TO configuration_id;
ALTER TABLE tenant ALTER COLUMN id SET NOT NULL;

ALTER TABLE configuration_with_uuid RENAME TO configuration;
ALTER TABLE configuration DROP COLUMN id;
ALTER TABLE configuration RENAME COLUMN uuid TO id;
ALTER TABLE configuration ALTER COLUMN id SET NOT NULL;

ALTER TABLE ONLY configuration
ADD CONSTRAINT pk_configuration PRIMARY KEY (id);

ALTER TABLE ONLY tenant
ADD CONSTRAINT pk_tenant PRIMARY KEY (id);

ALTER TABLE ONLY tenant
ADD CONSTRAINT tenant_default_host_key UNIQUE (default_host);

ALTER TABLE ONLY tenant
ADD CONSTRAINT tenant_slug_key UNIQUE (slug);

CREATE INDEX tenant_default_host_index ON tenant USING btree (default_host);

CREATE INDEX tenant_slug_index ON tenant USING btree (slug);

ALTER TABLE ONLY tenant
ADD CONSTRAINT tenant_configuration_fk FOREIGN KEY (configuration_id) REFERENCES configuration(id);

ALTER TABLE ONLY entity
ADD CONSTRAINT entity_unique_slug_per_type_per_tenant UNIQUE (slug, type, tenant_id);

ALTER TABLE ONLY entity
ADD CONSTRAINT pk_entity PRIMARY KEY (id);

CREATE INDEX entity_slug_index ON entity USING btree (slug);

CREATE INDEX entity_tenant_index ON entity USING btree (tenant_id);

CREATE INDEX entity_type_index ON entity USING btree (type);

ALTER TABLE ONLY entity
ADD CONSTRAINT entity_parent_fk FOREIGN KEY (parent_id) REFERENCES entity(id);

ALTER TABLE ONLY entity
ADD CONSTRAINT entity_tenant_fk FOREIGN KEY (tenant_id) REFERENCES tenant(id);

ALTER TABLE ONLY translation
ADD CONSTRAINT pk_translation PRIMARY KEY (id);

ALTER TABLE ONLY translation
ADD CONSTRAINT translation_unique_translation_per_field_per_entity UNIQUE (entity_id, field);

CREATE INDEX translation_field_index ON translation USING btree (field);

ALTER TABLE ONLY translation
ADD CONSTRAINT translation_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY translation_small
ADD CONSTRAINT pk_translation_small PRIMARY KEY (translation_id, locale);

CREATE INDEX translation_small_lang_index ON translation_small USING btree (locale);

ALTER TABLE ONLY translation_small
ADD CONSTRAINT translation_small_translation_fk FOREIGN KEY (translation_id) REFERENCES translation(id);

ALTER TABLE ONLY translation_medium
ADD CONSTRAINT pk_translation_medium PRIMARY KEY (translation_id, locale);

CREATE INDEX translation_medium_lang_index ON translation_medium USING btree (locale);

ALTER TABLE ONLY translation_medium
ADD CONSTRAINT translation_medium_translation_fk FOREIGN KEY (translation_id) REFERENCES translation(id);
