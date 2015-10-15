--
-- Creates the table holding invoice numbers
--

CREATE TABLE IF NOT EXISTS invoice_number (
    order_id uuid NOT NULL,
    number character varying(255) NOT NULL,
    generation_date timestamp with time zone
);

ALTER TABLE ONLY invoice_number DROP CONSTRAINT IF EXISTS invoice__number_order_id_fk;
ALTER TABLE ONLY invoice_number
ADD CONSTRAINT invoice__number_order_id_fk FOREIGN KEY (order_id) REFERENCES purchase_order(entity_id);

DROP INDEX IF EXISTS invoice_number_number_index;
CREATE INDEX invoice_number_number_index ON invoice_number USING btree (number);