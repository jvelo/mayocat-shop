-- Credits to https://gist.github.com/pozs/a632be48346ca2990a0e

-- SQL function to set a key to a specific value within a json object
-- requires PostgreSQL 9.3+
-- See https://gist.githubusercontent.com/pozs/a632be48346ca2990a0e/raw/dc9b1a4cd957fc8ea5f4096a88fb7a5ca5802537/json_object_set_key.sql

CREATE OR REPLACE FUNCTION "json_object_set_key"(
  "json"          json,
  "key_to_set"    TEXT,
  "value_to_set"  anyelement
)
  RETURNS json
  LANGUAGE sql
  IMMUTABLE
  STRICT
AS $function$
SELECT COALESCE(
  (SELECT ('{' || string_agg(to_json("key") || ':' || "value", ',') || '}')
     FROM (SELECT *
             FROM json_each("json")
            WHERE "key" <> "key_to_set"
            UNION ALL
           SELECT "key_to_set", to_json("value_to_set")) AS "fields"),
  '{}'
)::json
$function$;

-- Aggregate function to aggregate key-value pairs to json object (opposite of json_each())
-- requires PostgreSQL 9.3+ (but < 9.4!)
-- requires function "json_object_set_key"
-- See https://gist.githubusercontent.com/pozs/a632be48346ca2990a0e/raw/fbdfeba36ab3e3ee316cf1df7201b948f675e027/json_object_agg.sql

DROP AGGREGATE IF EXISTS "json_object_agg" (TEXT, anyelement);

CREATE AGGREGATE "json_object_agg" (TEXT, anyelement)
(
  STYPE     = json,
  SFUNC     = "json_object_set_key",
  INITCOND  = '{}'
);