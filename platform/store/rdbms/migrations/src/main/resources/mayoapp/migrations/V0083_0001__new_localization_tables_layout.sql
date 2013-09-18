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

CREATE OR REPLACE FUNCTION upsert_translation( in_entity_id uuid, in_locale text, in_entity json ) RETURNS void
  LANGUAGE plpgsql
  AS $$
BEGIN
    UPDATE localized_entity set entity = in_entity WHERE entity_id = in_entity_id and locale = in_locale;
    IF FOUND THEN
        RETURN;
    END IF;
    BEGIN
        INSERT INTO localized_entity (entity_id, locale, entity) values (in_entity_id, in_locale, in_entity);
    EXCEPTION WHEN OTHERS THEN
        UPDATE localized_entity set entity = in_entity WHERE entity_id = in_entity_id and locale = in_locale;
    END;
    RETURN;
END;
$$;
