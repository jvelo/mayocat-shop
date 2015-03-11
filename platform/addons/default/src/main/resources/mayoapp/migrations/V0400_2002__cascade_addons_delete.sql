ALTER TABLE addon
DROP CONSTRAINT addon_entity_fk,
ADD CONSTRAINT addon_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;