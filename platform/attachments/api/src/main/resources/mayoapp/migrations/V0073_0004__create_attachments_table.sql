--
-- Attachments
--

CREATE TABLE attachment (
    entity_id bigint NOT NULL,
    extension character varying(255) NOT NULL,
    title character varying(255),
    description text,
    data bytea
);

ALTER TABLE ONLY attachment
    ADD CONSTRAINT pk_attachment PRIMARY KEY (entity_id);

CREATE INDEX attachment_extension_index ON attachment USING btree (extension);

ALTER TABLE ONLY attachment
    ADD CONSTRAINT attachment_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);
