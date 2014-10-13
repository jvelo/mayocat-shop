ALTER TABLE entity_list
DROP CONSTRAINT entity_list_entity_id_fkey,
ADD CONSTRAINT entity_list_entity_id_fkey
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;
