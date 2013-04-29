CREATE TABLE attachment_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    extension character varying(255) NOT NULL,
    title character varying(255),
    description text,
    data bytea
);

INSERT INTO attachment_with_uuid (entity_id, extension, title, description, data)
SELECT entity_id, extension, title, description, data FROM attachment;

UPDATE attachment_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);