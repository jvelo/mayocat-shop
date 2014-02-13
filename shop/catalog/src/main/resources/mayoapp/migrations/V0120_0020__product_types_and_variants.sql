---
--- Add product_type and virtual columns to product
---

ALTER TABLE product ADD COLUMN product_type character varying(255);
ALTER TABLE product ADD COLUMN virtual boolean DEFAULT FALSE NOT NULL;
ALTER TABLE product ADD COLUMN features uuid[];

---
--- Create product_feature table
---

CREATE TABLE product_feature (
    entity_id uuid NOT NULL,
    feature character varying(255),
    feature_slug character varying(255),
    title character varying(255)
);

ALTER TABLE ONLY product_feature
    ADD CONSTRAINT pk_product_feature PRIMARY KEY (entity_id);

CREATE INDEX product_feature_feature_slug_index ON product_feature USING btree (feature_slug);

ALTER TABLE ONLY product_feature
    ADD CONSTRAINT product_feature_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;
