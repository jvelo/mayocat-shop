ALTER TABLE article
DROP CONSTRAINT article_entity_fk,
ADD CONSTRAINT article_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE article
DROP CONSTRAINT article_featured_image_fk,
ADD CONSTRAINT article_featured_image_fk
FOREIGN KEY (featured_image_id) REFERENCES entity(id) ON
DELETE CASCADE;