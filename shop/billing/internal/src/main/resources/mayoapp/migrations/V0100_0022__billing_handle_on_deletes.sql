ALTER TABLE customer
DROP CONSTRAINT customer_entity_fk,
ADD CONSTRAINT customer_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;

ALTER TABLE purchase_order
DROP CONSTRAINT order_customer_entity_fk,
ADD CONSTRAINT order_customer_entity_fk
FOREIGN KEY (customer_id) REFERENCES entity(id) ON DELETE SET NULL;

ALTER TABLE purchase_order
DROP CONSTRAINT order_billing_address_entity_fk,
ADD CONSTRAINT order_billing_address_entity_fk
FOREIGN KEY (billing_address_id) REFERENCES address(address_id) ON DELETE SET NULL;

ALTER TABLE purchase_order
DROP CONSTRAINT order_delivery_address_entity_fk,
ADD CONSTRAINT order_delivery_address_entity_fk
FOREIGN KEY (delivery_address_id) REFERENCES address(address_id) ON DELETE SET NULL;

ALTER TABLE purchase_order
DROP CONSTRAINT order_entity_fk,
ADD CONSTRAINT order_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;