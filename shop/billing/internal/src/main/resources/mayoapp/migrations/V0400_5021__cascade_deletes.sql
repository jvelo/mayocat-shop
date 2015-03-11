ALTER TABLE purchase_order_item
  DROP CONSTRAINT purchase_order_item_order_id_fkey;

ALTER TABLE purchase_order_item
  ADD CONSTRAINT purchase_order_item_order_id_fkey FOREIGN KEY (order_id) REFERENCES purchase_order (entity_id)
  ON DELETE CASCADE;

ALTER TABLE address
  DROP CONSTRAINT address_customer_fk;

ALTER TABLE address
  ADD CONSTRAINT address_customer_fk FOREIGN KEY (customer_id) REFERENCES customer(entity_id)
  ON DELETE CASCADE;
