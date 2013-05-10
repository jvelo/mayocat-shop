--
-- Agents (Correspond to user in Mayocat, but user is a reserved word)
--

CREATE TABLE agent (
    entity_id bigint NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255)
);

ALTER TABLE ONLY agent
    ADD CONSTRAINT pk_agent PRIMARY KEY (entity_id);

CREATE INDEX agent_email_index ON agent USING btree (email);

ALTER TABLE ONLY agent
    ADD CONSTRAINT agent_entity_fk FOREIGN KEY (entity_id) REFERENCES entity(id);

CREATE TABLE agent_role (
  id bigint NOT NULL,
  agent_id bigint,
  role character varying(255)
);

CREATE SEQUENCE agent_role_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE agent_role_id_seq OWNED BY agent_role.id;

ALTER TABLE ONLY agent_role ALTER COLUMN id SET DEFAULT nextval('agent_role_id_seq'::regclass);

ALTER TABLE ONLY agent_role
ADD CONSTRAINT pk_agent_role PRIMARY KEY (id);

ALTER TABLE ONLY agent_role
ADD CONSTRAINT agent_role_agent_kf FOREIGN KEY (agent_id) REFERENCES agent(entity_id);