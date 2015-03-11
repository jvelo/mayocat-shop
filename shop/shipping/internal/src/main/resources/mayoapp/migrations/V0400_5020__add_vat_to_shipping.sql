--
-- Adds vat rate column to carrier
--

ALTER TABLE ONLY carrier
ADD COLUMN vat_rate numeric(18,4);