--
-- Adds the metadata column to the attachment table
--

ALTER TABLE attachment ADD COLUMN metadata json;