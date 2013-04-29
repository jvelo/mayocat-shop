ALTER TABLE agent_with_uuid RENAME TO agent;
ALTER TABLE agent DROP COLUMN entity_id;
ALTER TABLE agent RENAME COLUMN entity_id_with_uuid TO entity_id;
ALTER TABLE agent ALTER COLUMN entity_id SET NOT NULL;

ALTER TABLE ONLY agent
    ADD CONSTRAINT pk_agent PRIMARY KEY (entity_id);

CREATE INDEX agent_email_index ON agent USING btree (email);

ALTER TABLE ONLY agent
    ADD CONSTRAINT agent_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

ALTER TABLE agent_role_with_uuid RENAME TO agent_role;
ALTER TABLE agent_role DROP COLUMN id, DROP COLUMN agent_id;
ALTER TABLE agent_role RENAME COLUMN uuid TO id;
ALTER TABLE agent_role RENAME COLUMN agent_id_with_uuid TO agent_id;
ALTER TABLE agent_role ALTER COLUMN id SET NOT NULL;

ALTER TABLE ONLY agent_role
ADD CONSTRAINT pk_agent_role PRIMARY KEY (id);

ALTER TABLE ONLY agent_role
ADD CONSTRAINT agent_role_agent_kf FOREIGN KEY (agent_id) REFERENCES agent(entity_id);
