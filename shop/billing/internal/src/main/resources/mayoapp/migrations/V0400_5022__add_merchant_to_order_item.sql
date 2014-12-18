--
-- Adds a de-normalized merchant name column to purchase order items
--

ALTER TABLE purchase_order_item ADD COLUMN merchant character varying(255);

UPDATE purchase_order_item AS i
SET merchant = t.name
FROM tenant AS t,
     entity AS e
WHERE e.id = i.purchasable_id
  AND e.tenant_id = t.entity_id;