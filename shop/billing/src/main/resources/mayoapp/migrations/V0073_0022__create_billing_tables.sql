--
-- Customer
--

CREATE TABLE customer (
    entity_id bigint NOT NULL,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255)
);

ALTER TABLE ONLY customer
    ADD CONSTRAINT pk_customer PRIMARY KEY (entity_id);

CREATE INDEX customer_email_index ON customer USING btree (email);

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

--
-- Address
--

CREATE TABLE address (
  address_id bigint NOT NULL,
  customer_id bigint,
  company character varying(255),
  full_name character varying(255),
  street character varying(255),
  street_complement character varying(255),
  zip character varying(255),
  city character varying(255),
  country character varying(255)
);

CREATE SEQUENCE address_address_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE address_address_id_seq OWNED BY address.address_id;

ALTER TABLE ONLY address ALTER COLUMN address_id SET DEFAULT nextval('address_address_id_seq'::regclass);

ALTER TABLE ONLY address
ADD CONSTRAINT pk_address PRIMARY KEY (address_id);

ALTER TABLE ONLY address
ADD CONSTRAINT address_customer_fk FOREIGN KEY (customer_id) REFERENCES customer(entity_id);

--
-- Purchase order
--

CREATE TABLE purchase_order (
  entity_id bigint NOT NULL,
  customer_id bigint,
  delivery_address_id bigint,
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

ALTER TABLE ONLY purchase_order
ADD CONSTRAINT pk_purchase_order PRIMARY KEY (entity_id);

ALTER TABLE ONLY purchase_order
ADD CONSTRAINT order_billing_address_entity_fk FOREIGN KEY (billing_address_id) REFERENCES address(address_id);

ALTER TABLE ONLY purchase_order
ADD CONSTRAINT order_customer_entity_fk FOREIGN KEY (customer_id) REFERENCES entity(id);

ALTER TABLE ONLY purchase_order
ADD CONSTRAINT order_delivery_address_entity_fk FOREIGN KEY (delivery_address_id) REFERENCES address(address_id);

ALTER TABLE ONLY purchase_order
ADD CONSTRAINT order_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);


--
-- Payment operation
--

CREATE TABLE payment_operation (
  operation_id bigint NOT NULL,
  order_id bigint,
  gateway_id character varying(255),
  external_id character varying(255),
  result character varying(255),
  memo text
);

CREATE SEQUENCE payment_operation_operation_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE payment_operation_operation_id_seq OWNED BY payment_operation.operation_id;

ALTER TABLE ONLY payment_operation ALTER COLUMN operation_id SET DEFAULT nextval('payment_operation_operation_id_seq'::regclass);

ALTER TABLE ONLY payment_operation
ADD CONSTRAINT pk_payment_operation PRIMARY KEY (operation_id);

ALTER TABLE ONLY payment_operation
ADD CONSTRAINT payment_operation_order_fk FOREIGN KEY (order_id) REFERENCES purchase_order(entity_id);
