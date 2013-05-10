CREATE TABLE configuration_with_uuid (
    uuid uuid,
    id bigint NOT NULL,
    version SMALLINT NOT NULL,
    DATA text
);

INSERT INTO configuration_with_uuid (id, version, DATA) SELECT id, version, DATA FROM configuration;

CREATE TABLE tenant_with_uuid (
  uuid uuid,
  id bigint NOT NULL,
  slug CHARACTER VARYING(255) NOT NULL,
  default_host CHARACTER VARYING(255),
  configuration_id bigint,
  configuration_with_uuid_id uuid
);

INSERT INTO tenant_with_uuid (id, slug, default_host, configuration_id) SELECT id, slug, default_host, configuration_id FROM tenant;

CREATE TABLE entity_with_uuid (
  uuid uuid,
  id bigint NOT NULL,
  slug CHARACTER VARYING(255) NOT NULL,
  type CHARACTER VARYING(255) NOT NULL,
  tenant_with_uuid_id uuid,
  tenant_id bigint,
  parent_with_uuid_id uuid,
  parent_id bigint
);

INSERT INTO entity_with_uuid (id, slug, type, tenant_id, parent_id)
SELECT id, slug, type, tenant_id, parent_id FROM entity;

CREATE TABLE translation_with_uuid (
  uuid uuid,
  id bigint NOT NULL,
  entity_id bigint,
  entity_with_uuid_id uuid,
  field character varying(255)
);

INSERT INTO translation_with_uuid (id, entity_id, field)
SELECT id, entity_id, field FROM translation;

-- We drop the ID field in favor of a combined key : translation_id + locale

CREATE TABLE translation_small_with_uuid (
  id bigint NOT NULL,
  translation_id bigint,
  translation_id_with_uuid uuid,
  locale character varying(255),
  text character varying(255)
);

INSERT INTO translation_small_with_uuid (id, translation_id, locale, text)
SELECT id, translation_id, locale, text FROM translation_small;

CREATE TABLE translation_medium_with_uuid (
  id bigint NOT NULL,
  translation_id bigint,
  translation_id_with_uuid uuid,
  locale character varying(255),
  text text
);

INSERT INTO translation_medium_with_uuid (id, translation_id, locale, text)
SELECT id, translation_id, locale, text FROM translation_medium;
