--
-- Configuration
--

CREATE TABLE configuration (
    id bigint NOT NULL,
    version smallint NOT NULL,
    data text
);

CREATE SEQUENCE configuration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE configuration_id_seq OWNED BY configuration.id;

ALTER TABLE ONLY configuration ALTER COLUMN id SET DEFAULT nextval('configuration_id_seq'::regclass);

ALTER TABLE ONLY configuration
    ADD CONSTRAINT pk_configuration PRIMARY KEY (id);

--
-- Tenant
--

CREATE TABLE tenant (
  id bigint NOT NULL,
  slug character varying(255) NOT NULL,
  default_host character varying(255),
  configuration_id bigint
);

CREATE SEQUENCE tenant_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE tenant_id_seq OWNED BY tenant.id;

ALTER TABLE ONLY tenant ALTER COLUMN id SET DEFAULT nextval('tenant_id_seq'::regclass);

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

--
-- Entity
--

CREATE TABLE entity (
  id bigint NOT NULL,
  slug character varying(255) NOT NULL,
  type character varying(255) NOT NULL,
  tenant_id bigint,
  parent_id bigint
);

CREATE SEQUENCE entity_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE entity_id_seq OWNED BY entity.id;

ALTER TABLE ONLY entity ALTER COLUMN id SET DEFAULT nextval('entity_id_seq'::regclass);

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

--
-- Translations
--

CREATE TABLE translation (
  id bigint NOT NULL,
  entity_id bigint,
  field character varying(255)
);

CREATE SEQUENCE translation_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE translation_id_seq OWNED BY translation.id;

ALTER TABLE ONLY translation ALTER COLUMN id SET DEFAULT nextval('translation_id_seq'::regclass);

ALTER TABLE ONLY translation
ADD CONSTRAINT pk_translation PRIMARY KEY (id);

ALTER TABLE ONLY translation
ADD CONSTRAINT translation_unique_translation_per_field_per_entity UNIQUE (entity_id, field);

CREATE INDEX translation_field_index ON translation USING btree (field);

ALTER TABLE ONLY translation
ADD CONSTRAINT translation_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

CREATE TABLE translation_small (
  id bigint NOT NULL,
  translation_id bigint,
  locale character varying(255),
  text character varying(255)
);

CREATE SEQUENCE translation_small_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE translation_small_id_seq OWNED BY translation_small.id;

ALTER TABLE ONLY translation_small ALTER COLUMN id SET DEFAULT nextval('translation_small_id_seq'::regclass);

ALTER TABLE ONLY translation_small
ADD CONSTRAINT pk_translation_small PRIMARY KEY (id);

ALTER TABLE ONLY translation_small
ADD CONSTRAINT translation_small_unique_translation_per_lang UNIQUE (translation_id, locale);

CREATE INDEX translation_small_lang_index ON translation_small USING btree (locale);

ALTER TABLE ONLY translation_small
ADD CONSTRAINT translation_small_translation_fk FOREIGN KEY (translation_id) REFERENCES translation(id);

CREATE TABLE translation_medium (
  id bigint NOT NULL,
  translation_id bigint,
  locale character varying(255),
  text text
);

CREATE SEQUENCE translation_medium_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE translation_medium_id_seq OWNED BY translation_medium.id;

ALTER TABLE ONLY translation_medium ALTER COLUMN id SET DEFAULT nextval('translation_medium_id_seq'::regclass);

ALTER TABLE ONLY translation_medium
ADD CONSTRAINT pk_translation_medium PRIMARY KEY (id);

ALTER TABLE ONLY translation_medium
ADD CONSTRAINT translation_medium_unique_translation_per_lang UNIQUE (translation_id, locale);

CREATE INDEX translation_medium_lang_index ON translation_medium USING btree (locale);

ALTER TABLE ONLY translation_medium
ADD CONSTRAINT translation_medium_translation_fk FOREIGN KEY (translation_id) REFERENCES translation(id);
