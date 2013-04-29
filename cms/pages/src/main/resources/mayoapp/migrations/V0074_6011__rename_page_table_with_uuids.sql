ALTER TABLE page_with_uuid RENAME TO page;
ALTER TABLE page DROP COLUMN entity_id, DROP COLUMN featured_image_id;
ALTER TABLE page RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE page RENAME COLUMN featured_image_id_with_uuid TO featured_image_id;
ALTER TABLE page ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY page
    ADD CONSTRAINT pk_page PRIMARY KEY (entity_id);

CREATE INDEX page_position_index ON page USING btree ("position");

CREATE INDEX page_published_index ON page USING btree (published);

ALTER TABLE ONLY page
    ADD CONSTRAINT page_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY page
    ADD CONSTRAINT page_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);
