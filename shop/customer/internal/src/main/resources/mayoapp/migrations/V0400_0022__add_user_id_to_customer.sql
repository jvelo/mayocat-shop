--
-- Adds agent_id column to customer table. Agent entity reference is optional : a customer is not necessarily linked
-- to an agent (i.e guest checkout)
--

ALTER TABLE customer ADD COLUMN agent_id uuid;

ALTER TABLE ONLY customer
ADD CONSTRAINT customer_agent_fk FOREIGN KEY (agent_id) REFERENCES agent(entity_id);
