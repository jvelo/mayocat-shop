CREATE TABLE new_addon (
  entity_id uuid,
  source character varying(255),
  addon_group character varying(255),
  model json,
  value json
);

INSERT INTO new_addon SELECT entity_id, source, addon_group, json_object_agg(addon_key, ('{"type":"' || type || '"}')::json), json_object_agg(addon_key , value) from addon GROUP BY entity_id, source, addon_group;

DROP TABLE addon;

ALTER TABLE new_addon
    RENAME TO addon;

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_unique_group_per_source_per_entity UNIQUE (entity_id, source, addon_group);

CREATE INDEX addon_group_index ON addon USING btree (addon_group);

CREATE INDEX addon_source_index ON addon USING btree (source);

ALTER TABLE ONLY addon
ADD CONSTRAINT addon_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);