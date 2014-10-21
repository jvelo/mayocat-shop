ALTER TABLE purchase_order ADD COLUMN items_total_excl numeric(18,4);
ALTER TABLE purchase_order ADD COLUMN shipping_excl numeric(18,4);
ALTER TABLE purchase_order ADD COLUMN grand_total_excl numeric(18,4);