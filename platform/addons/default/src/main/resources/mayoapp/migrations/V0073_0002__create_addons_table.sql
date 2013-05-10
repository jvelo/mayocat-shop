--
-- Addons
--

CREATE TABLE addon (
  entity_id bigint,
  source character varying(255),
  addon_group character varying(255),
  type character varying(255),
  addon_key character varying(255),
  value character varying(2000)
);

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_unique_addon_key_per_source_group_entity UNIQUE (entity_id, source, addon_group, addon_key);

CREATE INDEX addon_group_index ON addon USING btree (addon_group);

CREATE INDEX addon_key_index ON addon USING btree (addon_key);

CREATE INDEX addon_source_index ON addon USING btree (source);

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);