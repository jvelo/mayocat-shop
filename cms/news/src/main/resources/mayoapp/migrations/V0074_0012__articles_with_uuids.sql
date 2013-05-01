CREATE TABLE article_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    model character varying(255),
    published boolean,
    publication_date timestamp with time zone,
    title character varying(255),
    content text,
    featured_image_id_with_uuid uuid,
    featured_image_id bigint
);

INSERT INTO article_with_uuid (entity_id, model, published, publication_date, title, content, featured_image_id)
SELECT entity_id, model, published, publication_date, title, content, featured_image_id FROM article;

UPDATE article_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

UPDATE article_with_uuid SET featured_image_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = featured_image_id);