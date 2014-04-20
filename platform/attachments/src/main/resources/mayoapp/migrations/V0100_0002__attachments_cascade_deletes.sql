ALTER TABLE attachment
DROP CONSTRAINT attachment_entity_fk,
ADD CONSTRAINT attachment_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;

ALTER TABLE thumbnail
DROP CONSTRAINT thumbnail_image_fk,
ADD CONSTRAINT thumbnail_image_fk
FOREIGN KEY (attachment_id) REFERENCES attachment(entity_id) ON DELETE CASCADE;