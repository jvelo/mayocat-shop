CREATE TABLE carrier (
    tenant_id uuid,
    id uuid NOT NULL,
    destinations text,
    title character varying(255),
    strategy character varying(255),
    description text,
    minimum_days smallint,
    maximum_days smallint,
    per_shipping numeric(18,4),
    per_item numeric(18,4),
    per_additional_unit numeric(18,4)
);

ALTER TABLE ONLY carrier
ADD CONSTRAINT pk_carrier PRIMARY KEY (id);

CREATE TABLE carrier_rule (
    carrier_id uuid NOT NULL,
    up_to_value numeric(18,4),
    price numeric(18,4)
);

ALTER TABLE ONLY carrier_rule
ADD CONSTRAINT carrier_rule_carrier_fk FOREIGN KEY (carrier_id) REFERENCES carrier(id) ON DELETE CASCADE;