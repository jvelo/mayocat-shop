--
-- Adds column "active" and "validation_key" to the agent table.
-- Then activates all existing agents for backward compatibility
--

ALTER TABLE agent ADD COLUMN active boolean;
ALTER TABLE agent ADD COLUMN validation_key character varying(255);

UPDATE agent SET active = TRUE;