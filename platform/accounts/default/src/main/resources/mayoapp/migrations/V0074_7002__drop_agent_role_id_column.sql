ALTER TABLE agent_role DROP COLUMN id;

ALTER TABLE ONLY agent_role
ADD CONSTRAINT pk_agent_role PRIMARY KEY (agent_id, role);