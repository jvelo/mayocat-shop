--
-- thumbnail
--

CREATE TABLE thumbnail (
    attachment_id bigint NOT NULL,
    source character varying(255) NOT NULL,
    hint character varying(255) NOT NULL,
    ratio character varying(255),
    x integer,
    y integer,
    width integer,
    height integer
);

ALTER TABLE ONLY thumbnail
    ADD CONSTRAINT thumbnail_pk PRIMARY KEY (attachment_id, source, hint);

CREATE INDEX thumbnail_hint_index ON thumbnail USING btree (hint);

CREATE INDEX thumbnail_hint_ratio ON thumbnail USING btree (ratio);

CREATE INDEX thumbnail_hint_source ON thumbnail USING btree (source);

ALTER TABLE ONLY thumbnail
    ADD CONSTRAINT thumbnail_image_fk FOREIGN KEY (attachment_id) REFERENCES attachment(entity_id);
