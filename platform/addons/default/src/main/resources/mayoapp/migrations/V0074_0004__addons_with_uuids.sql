CREATE TABLE addon_with_uuid (
  entity_id_with_uuid uuid,
  entity_id bigint,
  source character varying(255),
  addon_group character varying(255),
  type character varying(255),
  addon_key character varying(255),
  value character varying(2000)
);

INSERT INTO addon_with_uuid (entity_id, source, addon_group, type, addon_key, value) select entity_id, source, addon_group, type, addon_key, value FROM addon;

UPDATE addon_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);