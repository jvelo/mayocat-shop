ALTER TABLE password_reset_request
  DROP CONSTRAINT password_reset_request_agent_id_fk;

ALTER TABLE password_reset_request
  ADD CONSTRAINT password_reset_request_agent_id_fk FOREIGN KEY (agent_id) REFERENCES agent(entity_id)
  ON DELETE CASCADE;
