--
-- Page
--

CREATE TABLE page (
    entity_id bigint NOT NULL,
    model character varying(255),
    published boolean,
    "position" smallint,
    title character varying(255),
    content text,
    featured_image_id bigint
);

ALTER TABLE ONLY page
    ADD CONSTRAINT pk_page PRIMARY KEY (entity_id);

CREATE INDEX page_position_index ON page USING btree ("position");

CREATE INDEX page_published_index ON page USING btree (published);

ALTER TABLE ONLY page
    ADD CONSTRAINT page_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY page
    ADD CONSTRAINT page_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);
