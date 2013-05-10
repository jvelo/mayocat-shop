--
-- Article
--

CREATE TABLE article (
    entity_id bigint NOT NULL,
    model character varying(255),
    published boolean,
    publication_date timestamp with time zone,
    title character varying(255),
    content text,
    featured_image_id bigint
);

ALTER TABLE ONLY article
    ADD CONSTRAINT pk_article PRIMARY KEY (entity_id);

CREATE INDEX article_publication_date_index ON article USING btree (publication_date);

CREATE INDEX article_published_index ON article USING btree (published);

ALTER TABLE ONLY article
    ADD CONSTRAINT article_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE ONLY article
    ADD CONSTRAINT article_featured_image_fk FOREIGN KEY (featured_image_id) REFERENCES entity(id);
