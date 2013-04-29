CREATE TABLE agent_with_uuid (
    entity_id_with_uuid uuid,
    entity_id bigint NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255)
);

INSERT INTO agent_with_uuid (entity_id, email, password)
SELECT entity_id, email, password FROM agent;

UPDATE agent_with_uuid SET entity_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = entity_id);

CREATE TABLE agent_role_with_uuid (
  uuid uuid,
  id bigint NOT NULL,
  agent_id_with_uuid uuid,
  agent_id bigint,
  role character varying(255)
);

INSERT INTO agent_role_with_uuid (id, agent_id, role)
SELECT id, agent_id, role FROM agent_role;

UPDATE agent_role_with_uuid SET agent_id_with_uuid = (SELECT uuid FROM entity_with_uuid WHERE id = agent_id);