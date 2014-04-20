ALTER TABLE agent
DROP CONSTRAINT agent_entity_fk,
ADD CONSTRAINT agent_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;

DO $$
  BEGIN
    IF EXISTS(SELECT 1 from pg_constraint where conname = 'agent_role_agent_kf')
      THEN
      ALTER TABLE agent_role
      DROP CONSTRAINT agent_role_agent_kf,
      ADD CONSTRAINT agent_role_agent_fk
      FOREIGN KEY (agent_id) REFERENCES agent(entity_id) ON DELETE CASCADE;
    END IF;
  END
$$;

ALTER TABLE tenant
DROP CONSTRAINT tenant_featured_image_fk,
ADD CONSTRAINT tenant_featured_image_fk
FOREIGN KEY (featured_image_id) REFERENCES entity(id) ON DELETE CASCADE;

ALTER TABLE tenant
DROP CONSTRAINT tenant_entity_fk,
ADD CONSTRAINT tenant_entity_fk
FOREIGN KEY (entity_id) REFERENCES entity(id) ON DELETE CASCADE;

ALTER TABLE entity
DROP CONSTRAINT entity_tenant_fk,
ADD CONSTRAINT entity_tenant_fk
FOREIGN KEY (tenant_id) REFERENCES tenant(entity_id) ON DELETE CASCADE;