CREATE TABLE customer_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255)
);

INSERT INTO customer_with_uuid (entity_id, email, first_name, last_name)
SELECT entity_id, email, first_name, last_name FROM customer;

UPDATE customer_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

CREATE TABLE address_with_uuid (
  address_id_with_uuid uuid,
  address_id bigint NOT NULL,
  customer_id_with_uuid uuid,
  customer_id bigint,
  company character varying(255),
  full_name character varying(255),
  street character varying(255),
  street_complement character varying(255),
  zip character varying(255),
  city character varying(255),
  country character varying(255)
);

INSERT INTO address_with_uuid (address_id, customer_id, company, full_name, street, street_complement, zip, city, country)
SELECT address_id, customer_id, company, full_name, street, street_complement, zip, city, country FROM address;

UPDATE address_with_uuid SET customer_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = customer_id);

CREATE TABLE purchase_order_with_uuid (
  entity_id_with_uuid uuid,
  entity_id bigint NOT NULL,
  customer_id_with_uuid uuid,
  customer_id bigint,
  delivery_address_id_with_uuid uuid,
  delivery_address_id bigint,
  billing_address_id_with_uuid uuid,
  billing_address_id bigint,
  creation_date timestamp with time zone,
  update_date timestamp with time zone,
  currency character varying(3),
  number_of_items smallint,
  items_total numeric(18,4),
  grand_total numeric(18,4),
  status character varying(32),
  order_data text
);

INSERT INTO purchase_order_with_uuid (entity_id, customer_id, delivery_address_id, billing_address_id, creation_date, update_date,
currency, number_of_items, items_total, grand_total, status, order_data)
SELECT entity_id, customer_id, delivery_address_id, billing_address_id, creation_date, update_date,
currency, number_of_items, items_total, grand_total, status, order_data FROM purchase_order;

UPDATE purchase_order_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

UPDATE purchase_order_with_uuid SET customer_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = customer_id);

CREATE TABLE payment_operation_with_uuid (
  operation_id_with_uuid uuid,
  operation_id bigint NOT NULL,
  order_id_with_uuid uuid,
  order_id bigint,
  gateway_id character varying(255),
  external_id character varying(255),
  result character varying(255),
  memo text
);

INSERT INTO payment_operation_with_uuid (operation_id, order_id, gateway_id, external_id, result, memo)
SELECT operation_id, order_id, gateway_id, external_id, result, memo FROM payment_operation;

UPDATE payment_operation_with_uuid SET order_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = order_id);