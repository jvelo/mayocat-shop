--- We're not going to use the "translation*" tables initially designed (but never actually used).
DROP TABLE translation_medium;
DROP TABLE translation_small;
DROP TABLE translation;

CREATE TABLE localized_entity(
  entity_id uuid,
  locale character varying(5),
  entity json,
  FOREIGN KEY (entity_id) REFERENCES entity(id)
);

CREATE INDEX localized_entity_locale ON localized_entity USING btree (locale);
