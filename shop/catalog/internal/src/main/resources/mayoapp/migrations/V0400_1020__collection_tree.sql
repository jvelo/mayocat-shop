--
-- Enable the ltree extension in database
--

CREATE EXTENSION ltree;

--
-- Create the new collection_entity table
--

CREATE TABLE collection_entity (
    entity_id uuid NOT NULL,
    collection_id uuid NOT NULL,
    path ltree,
    position smallint
);

--
-- Create constraints
--

ALTER TABLE ONLY collection_entity
    ADD CONSTRAINT collection_entity_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY collection_entity
    ADD CONSTRAINT collection_entity_collection_fk FOREIGN KEY (collection_id) REFERENCES collection(entity_id);

--
-- Create indexes
--
CREATE INDEX collection_entity_position_index ON collection_entity USING btree ("position");
CREATE INDEX collection_entity_collection_index ON collection_entity USING GIST(path);

--
-- Import back products
--

INSERT INTO collection_entity (entity_id, collection_id, path, position)
 SELECT product_id, collection_id, CAST(replace(collection_id::text, '-', '_') AS ltree), position FROM collection_product;
