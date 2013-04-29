ALTER TABLE attachment_with_uuid RENAME TO attachment;
ALTER TABLE attachment DROP COLUMN entity_id;
ALTER TABLE attachment RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE attachment ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY attachment
    ADD CONSTRAINT pk_attachment PRIMARY KEY (entity_id);

CREATE INDEX attachment_extension_index ON attachment USING btree (extension);

ALTER TABLE ONLY attachment
    ADD CONSTRAINT attachment_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);
