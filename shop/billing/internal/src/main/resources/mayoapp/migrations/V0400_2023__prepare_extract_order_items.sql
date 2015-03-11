--
-- Create the table that will hold order items data
--

CREATE TABLE purchase_order_item (
  id uuid PRIMARY KEY,
  order_id uuid REFERENCES purchase_order(entity_id),
  purchasable_id uuid,
  type character varying(32),
  title character varying(255),
  quantity smallint,
  unit_price numeric(18,4),
  item_total numeric(18,4),
  vat_rate numeric(18,4),
  data json
);
