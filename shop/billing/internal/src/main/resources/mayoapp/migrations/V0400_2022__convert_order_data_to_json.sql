--
-- Converts order data column from text to json
--

ALTER TABLE purchase_order ALTER COLUMN order_data TYPE json USING order_data::json;