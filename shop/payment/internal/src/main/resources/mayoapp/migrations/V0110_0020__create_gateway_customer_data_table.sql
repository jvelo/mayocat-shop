CREATE TABLE gateway_customer_data (
    customer_id uuid NOT NULL,
    gateway character varying(255),
    customer_data text

    CONSTRAINT gateway_customer_data_customer_fk FOREIGN KEY (customer_id) REFERENCES customer(entity_id) ON DELETE CASCADE
);

CREATE INDEX gateway_customer_data_gateway_index ON gateway_customer_data USING btree (gateway);