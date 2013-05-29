CREATE TABLE thumbnail_with_uuid (
    attachment_id_with_uuid uuid,
    attachment_id bigint NOT NULL,
    source character varying(255) NOT NULL,
    hint character varying(255) NOT NULL,
    ratio character varying(255),
    x integer,
    y integer,
    width integer,
    height integer
);

INSERT INTO thumbnail_with_uuid (attachment_id, source, hint, ratio, x, y, width, height)
SELECT attachment_id, source, hint, ratio, x, y, width, height FROM thumbnail;

UPDATE thumbnail_with_uuid SET attachment_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = attachment_id);