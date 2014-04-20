--
-- Create the entity_list table
--

CREATE TABLE entity_list(
  entity_id uuid,
  entity_type character varying(255),
  entities uuid[],
  hint character varying(255),
  FOREIGN KEY (entity_id) REFERENCES entity(id)
);


--
-- Create an operator class for UUID so that we can create and index on the entity list array
--
-- Credits to http://stackoverflow.com/a/19959736
--
CREATE OPERATOR CLASS _uuid_ops DEFAULT
  FOR TYPE _uuid USING gin AS
  OPERATOR 1 &&(anyarray, anyarray),
  OPERATOR 2 @>(anyarray, anyarray),
  OPERATOR 3 <@(anyarray, anyarray),
  OPERATOR 4 =(anyarray, anyarray),
  FUNCTION 1 uuid_cmp(uuid, uuid),
  FUNCTION 2 ginarrayextract(anyarray, internal, internal),
  FUNCTION 3 ginqueryarrayextract(anyarray, internal, smallint, internal, internal, internal, internal),
  FUNCTION 4 ginarrayconsistent(internal, smallint, anyarray, integer, internal, internal, internal, internal),
  STORAGE uuid;


--
-- Index entities UUID so that we can do reverse lookups : find lists that contain an entity with that id (useful
-- for cleaning lists upon deletion).
--
CREATE INDEX entity_list_entity_id_index on entity_list USING gin (entities);
