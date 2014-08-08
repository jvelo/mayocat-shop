ALTER TABLE collection
DROP CONSTRAINT collection_entity_fk,
ADD CONSTRAINT collection_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE collection
DROP CONSTRAINT collection_featured_image_fk,
ADD CONSTRAINT collection_featured_image_fk
FOREIGN KEY (featured_image_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE collection_product
DROP CONSTRAINT collection_product_collection_entity_fk,
ADD CONSTRAINT collection_product_collection_entity_fk
FOREIGN KEY (collection_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE collection_product
DROP CONSTRAINT collection_product_product_entity_fk,
ADD CONSTRAINT collection_product_product_entity_fk
FOREIGN KEY (product_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE product
DROP CONSTRAINT product_entity_fk,
ADD CONSTRAINT product_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON
DELETE CASCADE;

ALTER TABLE product
DROP CONSTRAINT product_featured_image_fk,
ADD CONSTRAINT product_featured_image_fk
FOREIGN KEY (featured_image_id) REFERENCES entity(id) ON
DELETE CASCADE;