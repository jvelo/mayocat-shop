--
-- Re-create constraints with proper on delete cascade
--

ALTER TABLE collection_entity
DROP CONSTRAINT collection_entity_entity_fk,
ADD CONSTRAINT collection_entity_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;

ALTER TABLE ONLY collection_entity
DROP CONSTRAINT collection_entity_collection_fk,
ADD CONSTRAINT collection_entity_collection_fk FOREIGN KEY (collection_id) REFERENCES collection(entity_id) ON DELETE CASCADE;