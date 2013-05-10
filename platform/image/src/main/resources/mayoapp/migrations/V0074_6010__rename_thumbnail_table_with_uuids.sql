ALTER TABLE thumbnail_with_uuid RENAME TO thumbnail;
ALTER TABLE thumbnail DROP COLUMN attachment_id;
ALTER TABLE thumbnail RENAME COLUMN attachment_id_with_uuid TO attachment_id;
ALTER TABLE thumbnail ALTER COLUMN attachment_id SET NOT NULL;

ALTER TABLE ONLY thumbnail
    ADD CONSTRAINT thumbnail_pk PRIMARY KEY (attachment_id, source, hint);

CREATE INDEX thumbnail_hint_index ON thumbnail USING btree (hint);

CREATE INDEX thumbnail_hint_ratio ON thumbnail USING btree (ratio);

CREATE INDEX thumbnail_hint_source ON thumbnail USING btree (source);

ALTER TABLE ONLY thumbnail
    ADD CONSTRAINT thumbnail_image_fk FOREIGN KEY (attachment_id) REFERENCES attachment(entity_id);
