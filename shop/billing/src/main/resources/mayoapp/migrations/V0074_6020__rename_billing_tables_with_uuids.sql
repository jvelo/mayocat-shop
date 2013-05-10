ALTER TABLE customer_with_uuid RENAME TO customer;
ALTER TABLE customer DROP COLUMN entity_id;
ALTER TABLE customer RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE customer ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY customer
    ADD CONSTRAINT pk_customer PRIMARY KEY (entity_id);

CREATE INDEX customer_email_index ON customer USING btree (email);

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE address_with_uuid RENAME TO address;
ALTER TABLE address DROP COLUMN address_id, DROP COLUMN customer_id;
ALTER TABLE address RENAME COLUMN address_id_with_uuid TO address_id;
ALTER TABLE address RENAME COLUMN customer_id_with_uuid TO customer_id;
ALTER TABLE address ALTER COLUMN address_id SET NOT NULL;

ALTER TABLE ONLY address
ADD CONSTRAINT pk_address PRIMARY KEY (address_id);

ALTER TABLE ONLY address
ADD CONSTRAINT address_customer_fk FOREIGN KEY (customer_id) REFERENCES customer(entity_id);

ALTER TABLE purchase_order_with_uuid RENAME TO purchase_order;
ALTER TABLE purchase_order DROP COLUMN entity_id, DROP COLUMN customer_id, DROP COLUMN delivery_address_id, DROP COLUMN billing_address_id;
ALTER TABLE purchase_order RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE purchase_order RENAME COLUMN customer_id_with_uuid TO customer_id;
ALTER TABLE purchase_order RENAME COLUMN delivery_address_id_with_uuid TO delivery_address_id;
ALTER TABLE purchase_order RENAME COLUMN billing_address_id_with_uuid TO billing_address_id;
ALTER TABLE purchase_order ALTER COLUMN entity_id SET NOT NULL;

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

ALTER TABLE payment_operation_with_uuid RENAME to payment_operation;
ALTER TABLE payment_operation DROP COLUMN operation_id, DROP COLUMN order_id;
ALTER TABLE payment_operation RENAME COLUMN operation_id_with_uuid TO operation_id;
ALTER TABLE payment_operation RENAME COLUMN order_id_with_uuid TO order_id;
ALTER TABLE payment_operation ALTER COLUMN operation_id SET NOT NULL;

ALTER TABLE ONLY payment_operation
ADD CONSTRAINT pk_payment_operation PRIMARY KEY (operation_id);

ALTER TABLE ONLY payment_operation
ADD CONSTRAINT payment_operation_order_fk FOREIGN KEY (order_id) REFERENCES purchase_order(entity_id);
