--
-- Insert entity if it does not exist, see EntityDAO#createEntityIfItDoesNotExist
--
CREATE OR REPLACE FUNCTION insert_entity_if_not_exist( in_entity_id uuid, in_slug character varying, in_type character varying, in_entity_tenant uuid) RETURNS integer
  LANGUAGE plpgsql
  AS $$
BEGIN
  BEGIN
    INSERT INTO entity (id, slug, type, tenant_id) VALUES (in_entity_id, in_slug, in_type, in_entity_tenant);
      RETURN (SELECT 1);
    EXCEPTION WHEN OTHERS
    THEN
      RETURN (SELECT 0);
  END;
END;
$$;

--
-- Insert child entity if it does not exist, see EntityDAO#createChildEntityIfItDoesNotExist
--
CREATE OR REPLACE FUNCTION insert_child_entity_if_not_exist(in_entity_id uuid, in_slug character varying, in_type character varying, in_entity_tenant uuid, in_entity_parent uuid) RETURNS integer
LANGUAGE plpgsql
AS $$
BEGIN
  BEGIN
    INSERT INTO entity (id, slug, type, tenant_id, parent_id) VALUES (in_entity_id, in_slug, in_type, in_entity_tenant, in_entity_parent);
      RETURN (SELECT 1);
    EXCEPTION WHEN OTHERS
    THEN
      RETURN (SELECT 0);
  END;
END;
$$;