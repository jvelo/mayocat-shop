ALTER TABLE localized_entity
DROP CONSTRAINT localized_entity_entity_id_fkey,
ADD CONSTRAINT localized_entity_entity_id_fkey
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;