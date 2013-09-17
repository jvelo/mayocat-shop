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

/* Given an entity_id retrieves a JSON array of the localization data available for this entity */
CREATE OR REPLACE FUNCTION localization_data(the_entity_id uuid) RETURNS json
    LANGUAGE sql
    AS $$
  SELECT array_to_json(array_agg(row_to_json(l)))
  FROM (
      SELECT locale, entity
      FROM localized_entity
      WHERE localized_entity.entity_id = the_entity_id
  ) l
$$;
