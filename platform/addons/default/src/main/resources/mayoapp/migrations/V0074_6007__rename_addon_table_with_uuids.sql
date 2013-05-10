ALTER TABLE addon_with_uuid RENAME TO addon;
ALTER TABLE addon DROP COLUMN entity_id;
ALTER TABLE addon RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE addon ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_unique_addon_key_per_source_group_entity UNIQUE (entity_id, source, addon_group, addon_key);

CREATE INDEX addon_group_index ON addon USING btree (addon_group);

CREATE INDEX addon_key_index ON addon USING btree (addon_key);

CREATE INDEX addon_source_index ON addon USING btree (source);

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);