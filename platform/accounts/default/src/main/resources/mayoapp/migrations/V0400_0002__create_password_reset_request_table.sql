--
-- Creates the table holding password reset requests
--

CREATE TABLE password_reset_request (
    agent_id uuid NOT NULL,
    reset_key character varying(255) NOT NULL
);

ALTER TABLE ONLY password_reset_request
ADD CONSTRAINT password_reset_request_agent_id_fk FOREIGN KEY (agent_id) REFERENCES agent(entity_id);