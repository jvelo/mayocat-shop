--
-- Adds a generic data column to purchase orders
--

ALTER TABLE purchase_order ADD COLUMN data json;