ALTER TABLE page
DROP CONSTRAINT page_entity_fk,
ADD CONSTRAINT page_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE page
DROP CONSTRAINT page_featured_image_fk,
ADD CONSTRAINT page_featured_image_fk
FOREIGN KEY (featured_image_id) REFERENCES entity(id) ON
DELETE CASCADE;