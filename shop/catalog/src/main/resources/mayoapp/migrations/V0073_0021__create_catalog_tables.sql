--
-- Product
--

CREATE TABLE product (
    entity_id bigint NOT NULL,
    model character varying(255),
    on_shelf boolean,
    "position" smallint,
    title character varying(255),
    description text,
    price numeric(18,4),
    stock smallint,
    featured_image_id bigint
);

ALTER TABLE ONLY product
    ADD CONSTRAINT pk_product PRIMARY KEY (entity_id);

CREATE INDEX product_on_shelf_index ON product USING btree (on_shelf);

CREATE INDEX product_position_index ON product USING btree ("position");

ALTER TABLE ONLY product
    ADD CONSTRAINT product_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY product
    ADD CONSTRAINT product_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);

--
-- Collection
--

CREATE TABLE collection (
    entity_id bigint NOT NULL,
    "position" smallint,
    title character varying(255),
    description text,
    featured_image_id bigint
);

ALTER TABLE ONLY collection
    ADD CONSTRAINT pk_collection PRIMARY KEY (entity_id);

CREATE INDEX collection_position_index ON collection USING btree ("position");

ALTER TABLE ONLY collection
    ADD CONSTRAINT collection_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY collection
    ADD CONSTRAINT collection_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);

--
-- Collection <-> Product
--

CREATE TABLE collection_product (
  collection_id bigint NOT NULL,
  product_id bigint NOT NULL,
  "position" smallint
);

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_pk PRIMARY KEY (collection_id, product_id);

CREATE INDEX collection_product_position_index ON collection_product USING btree ("position");

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_collection_entity_fk FOREIGN KEY (collection_id) REFERENCES entity(id);

ALTER TABLE ONLY collection_product
ADD CONSTRAINT collection_product_product_entity_fk FOREIGN KEY (product_id) REFERENCES entity(id);
