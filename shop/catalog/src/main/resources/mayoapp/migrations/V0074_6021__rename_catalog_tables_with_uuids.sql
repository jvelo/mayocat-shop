ALTER TABLE collection_with_uuid RENAME TO collection;
ALTER TABLE collection DROP COLUMN entity_id, DROP COLUMN featured_image_id;
ALTER TABLE collection RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE collection RENAME COLUMN featured_image_id_with_uuid TO featured_image_id;
ALTER TABLE collection ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY collection
ADD CONSTRAINT pk_collection PRIMARY KEY (entity_id);

CREATE INDEX collection_position_index ON collection USING btree ("position");

ALTER TABLE ONLY collection
ADD CONSTRAINT collection_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY collection
ADD CONSTRAINT collection_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);

ALTER TABLE product_with_uuid RENAME TO product;
ALTER TABLE product DROP COLUMN entity_id, DROP COLUMN featured_image_id;
ALTER TABLE product RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE product RENAME COLUMN featured_image_id_with_uuid TO featured_image_id;
ALTER TABLE product ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY product
ADD CONSTRAINT pk_product PRIMARY KEY (entity_id);

CREATE INDEX product_on_shelf_index ON product USING btree (on_shelf);

CREATE INDEX product_position_index ON product USING btree ("position");

ALTER TABLE ONLY product
ADD CONSTRAINT product_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY product
ADD CONSTRAINT product_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);

ALTER TABLE collection_product_with_uuid RENAME TO collection_product;
ALTER TABLE collection_product DROP COLUMN collection_id, DROP COLUMN product_id;
ALTER TABLE collection_product RENAME COLUMN collection_id_with_uuid TO collection_id;
ALTER TABLE collection_product RENAME COLUMN product_id_with_uuid TO product_id;
ALTER TABLE collection_product ALTER COLUMN collection_id SET NOT NULL;
ALTER TABLE collection_product ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_pk PRIMARY KEY (collection_id, product_id);

CREATE INDEX collection_product_position_index ON collection_product USING btree ("position");

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_collection_entity_fk FOREIGN KEY (collection_id) REFERENCES entity(id);

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_product_entity_fk FOREIGN KEY (product_id) REFERENCES entity(id);
