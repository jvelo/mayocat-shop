ALTER TABLE payment_operation 
  DROP CONSTRAINT payment_operation_order_fk; 

ALTER TABLE payment_operation 
  ADD CONSTRAINT payment_operation_order_fk FOREIGN KEY (order_id) REFERENCES 
  purchase_order (entity_id) ON DELETE CASCADE; 