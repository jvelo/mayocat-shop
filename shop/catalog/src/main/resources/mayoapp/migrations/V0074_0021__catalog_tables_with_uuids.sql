CREATE TABLE product_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    model character varying(255),
    on_shelf boolean,
    "position" smallint,
    title character varying(255),
    description text,
    price numeric(18,4),
    stock smallint,
    featured_image_id_with_uuid uuid,
    featured_image_id bigint
);

INSERT INTO product_with_uuid (entity_id, model, on_shelf, position, title, description, price, stock, featured_image_id)
SELECT entity_id, model, on_shelf, position, title, description, price, stock, featured_image_id FROM product;

UPDATE product_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

UPDATE product_with_uuid SET featured_image_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = featured_image_id);

-- For the collection table we take this opportunity to insert a "model" field for future use

CREATE TABLE collection_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    model character varying(255),
    "position" smallint,
    title character varying(255),
    description text,
    featured_image_id_with_uuid uuid,
    featured_image_id bigint
);

INSERT INTO collection_with_uuid (entity_id, position, title, description, featured_image_id)
SELECT entity_id, position, title, description, featured_image_id FROM collection;

UPDATE collection_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

UPDATE collection_with_uuid SET featured_image_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = featured_image_id);

CREATE TABLE collection_product_with_uuid (
  collection_id_with_uuid uuid,
  collection_id bigint NOT NULL,
  product_id_with_uuid uuid,
  product_id bigint NOT NULL,
  "position" smallint
);

UPDATE collection_product_with_uuid SET collection_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = collection_id);
UPDATE collection_product_with_uuid SET product_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = product_id);