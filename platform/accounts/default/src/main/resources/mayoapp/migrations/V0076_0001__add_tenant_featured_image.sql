ALTER TABLE tenant
ADD COLUMN featured_image_id uuid;

ALTER TABLE ONLY tenant
ADD CONSTRAINT tenant_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);