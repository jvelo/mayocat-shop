ALTER TABLE article_with_uuid RENAME TO article;
ALTER TABLE article DROP COLUMN entity_id, DROP COLUMN featured_image_id;
ALTER TABLE article RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE article RENAME COLUMN featured_image_id_with_uuid TO featured_image_id;
ALTER TABLE article ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY article
    ADD CONSTRAINT pk_article PRIMARY KEY (entity_id);

CREATE INDEX article_publication_date_index ON article USING btree (publication_date);

CREATE INDEX article_published_index ON article USING btree (published);

ALTER TABLE ONLY article
    ADD CONSTRAINT article_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY article
    ADD CONSTRAINT article_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);
